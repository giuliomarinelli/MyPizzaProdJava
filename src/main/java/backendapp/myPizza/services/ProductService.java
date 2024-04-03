package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.*;
import backendapp.myPizza.Models.enums.ToppingType;
import backendapp.myPizza.Models.reqDTO.ManyProductsPostDTO;
import backendapp.myPizza.Models.reqDTO.ProductDTO;
import backendapp.myPizza.Models.reqDTO.ToppingDTO;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.repositories.CategoryRepository;
import backendapp.myPizza.repositories.MenuRepository;
import backendapp.myPizza.repositories.ProductRepository;
import backendapp.myPizza.repositories.ToppingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class ProductService {

    @Autowired
    private ToppingRepository toppingRp;

    @Autowired
    private ProductRepository productRp;

    @Autowired
    private CategoryRepository categoryRp;

    @Autowired
    private MenuRepository menuRp;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    // Tutti i topping di tipo TOPPING
    public List<Topping> findAllToppingToppings() {
        return toppingRp.findAll().stream().filter(t -> t.getType().equals(ToppingType.TOPPING))
                .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)")).toList();
    }

    // Tutti i topping di tipo EXTRA
    public List<Topping> findAllExtras() {
        return toppingRp.findAll().stream().filter(t -> t.getType().equals(ToppingType.EXTRA))
                .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)")).toList();
    }

    // Tutti i topping
    public List<Topping> findAllToppings() {
        return toppingRp.findAll().stream()
                .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)")).toList();
    }

    public Topping addTopping(ToppingDTO t) throws BadRequestException {
        List<String> existingToppingNames = toppingRp.findAllToppingNames();
        // Duplicate exception handling
        if (existingToppingNames.contains(t.name()))
            throw new BadRequestException("Topping with name '" + t.name() + "' already exists");
        Topping topping = new Topping(t.name(), t.price(), t.type());
        toppingRp.save(topping);
        topping.setDescription(topping.getName() + " (" + df.format(topping.getPrice()) + "€)");
        return topping;
    }

    public Topping updateToppingByName(String oldName, ToppingDTO toppingDTO) throws BadRequestException {
        Topping oldTopping = toppingRp.findByName(oldName).orElseThrow(
                () -> new BadRequestException("Topping you're trying to update doesn't exist")
        );
        for (Product p : oldTopping.getProducts()) {
            p.getToppings().remove(oldTopping);
            productRp.save(p);
        }
        oldTopping.setName(toppingDTO.name());
        oldTopping.setPrice(toppingDTO.price());
        oldTopping.setType(toppingDTO.type());
        toppingRp.save(oldTopping);
        oldTopping.setDescription(oldTopping.getName() + " (" + df.format(oldTopping.getPrice()) + "€)");
        return oldTopping;
    }


    public ConfirmRes deleteToppingByName(String name) throws BadRequestException {
        Topping topping = toppingRp.findByName(name).orElseThrow(
                () -> new BadRequestException("Topping you're trying to delete doesn't exist")
        );
        for (Product p : topping.getProducts()) {
            p.getToppings().remove(topping);
            productRp.save(p);
        }
        toppingRp.delete(topping);
        return new ConfirmRes("Topping '" + name + "' has been successfully deleted", HttpStatus.OK);
    }

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRp.findAll(pageable).map(p -> {
            p.setToppings(p.getToppings().stream()
                    .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)")).toList());
            p.setProductTotalAmount();
            return p;
        });
    }

    public Product updateProductByName(String oldName, ProductDTO productDTO) throws BadRequestException, NotFoundException {

        Product oldProduct = productRp.findByName(oldName).orElseThrow(
                () -> new BadRequestException("The product you're trying to update (name: '" + oldName + "') doesn't exist")
        );
        assert menuRp.findByItemId(oldProduct.getId()).isPresent();
        Menu menu = menuRp.findByItemId(oldProduct.getId()).get();
        menuRp.delete(menu);
        oldProduct.setName(productDTO.name());
        oldProduct.setBasePrice(productDTO.basePrice());
        Optional<Category> sentCategory = categoryRp.findByName(productDTO.category());
        Category oldCategory = oldProduct.getCategory();
        if (sentCategory.isPresent() && !sentCategory.get().equals(oldCategory)) {
            oldProduct.setCategory(sentCategory.get());
            productRp.save(oldProduct);
        } else if (sentCategory.isEmpty()) {
            Category newCategory = new Category(productDTO.category());
            categoryRp.save(newCategory);;
            menuRp.save(new Menu(newCategory));
            oldProduct.setCategory(newCategory);
            productRp.save(oldProduct);
        }

        if (productsHaveNotCategory(oldCategory)) {
            categoryRp.delete(oldCategory);
            assert menuRp.findByItemId(oldCategory.getId()).isPresent();
            Menu oldMenu = menuRp.findByItemId(oldCategory.getId()).get();
            menuRp.delete(oldMenu);
        }


        List<Topping> toppings = new ArrayList<>();

        for (String name : productDTO.toppings()) {
            Topping t = toppingRp.findByName(name).orElseThrow(
                    () -> new BadRequestException("Topping (old name: '" + name + "') you're trying to update doesn't exist")
            );
            t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)");
            toppings.add(t);
        }
        oldProduct.getToppings().clear();
        for (Topping t : toppings) {
            oldProduct.getToppings().add(t);
        }

        productRp.save(oldProduct);
        menuRp.save(new Menu(oldProduct));
        oldProduct.setProductTotalAmount();
        return oldProduct;
    }

    public ConfirmRes deleteProductByName(String name) throws BadRequestException {
        Product product = productRp.findByName(name).orElseThrow(
                () -> new BadRequestException("The product you're trying to delete doesn't exist")
        );
        Category category = product.getCategory();

        assert menuRp.findByItemId(product.getId()).isPresent();
        menuRp.delete(menuRp.findByItemId(product.getId()).get());

        productRp.delete(product);
        if (productsHaveNotCategory(category)) {
            assert menuRp.findByItemId(category.getId()).isPresent();
            menuRp.delete(menuRp.findByItemId(category.getId()).get());
            categoryRp.delete(category);
        }

        return new ConfirmRes("Product with name '" + name + "' deleted successfully", HttpStatus.OK);
    }

    public List<String> getAllCategoryNames() {
        ArrayList<String> categories = (ArrayList<String>) productRp.getAllCategoryNames();
        categories.add(0,"(seleziona una categoria)");
        categories.add(categories.size() - 1, "- Inserisci nuova -");
        return categories;
    }

    public boolean isPresentCategory(String name) {
        return categoryRp.findByName(name).isPresent();
    }

    public boolean productsHaveNotCategory(Category category) {
        return productRp.findAll().stream().noneMatch(p -> p.getCategory().equals(category));
    }

    public List<String> getProductNames() {
        return productRp.findAll().stream().map(Product::getName).toList();
    }

    public List<Product> addManyProducts(ManyProductsPostDTO manyProductsPostDTO) throws BadRequestException {
        List<ProductDTO> products = manyProductsPostDTO.products();
        // Handling exceptions before making db transactions
        for (ProductDTO p : products) {
            Optional<Product> product = productRp.findByName(p.name());
            if (product.isPresent())
                throw new BadRequestException("Product with name '" + p.name() + "' already exists");
            List<String> toppingsNames = p.toppings();
            for (String name : toppingsNames) {
                toppingRp.findByName(name).orElseThrow(
                        () -> new BadRequestException("Topping '" + name + "' doesn't exist")
                );
            }
        }
        // Handling exception finish
        List<Product> addedProducts = new ArrayList<>();
        for (ProductDTO p : products) {
            Category category;
            if (isPresentCategory(p.category())) {
                assert categoryRp.findByName(p.category()).isPresent();
                category = categoryRp.findByName(p.category()).get();

            } else {
                category = new Category(p.category());
                categoryRp.save(category);
                Menu menu = new Menu();
                menu.setItem(category);
                menuRp.save(menu);
            }

            Product newProduct = new Product(p.name(), p.basePrice(), category);


            for (String name : p.toppings()) {
                assert toppingRp.findByName(name).isPresent();
                newProduct.getToppings().add(toppingRp.findByName(name).get());
            }
            addedProducts.add(newProduct);
        }
        productRp.saveAll(addedProducts);
        List<Menu> menuProducts = new ArrayList<>();
        for (Product p : addedProducts) {
            menuProducts.add(new Menu(p));
        }
        menuRp.saveAll(menuProducts);
        return addedProducts.stream()
                .peek(p -> p.setToppings(p.getToppings().stream()
                        .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)"))
                        .toList())).toList();
    }

    public Product addProduct(ProductDTO productDTO) throws BadRequestException {
        Product p = new Product();
        p.setName(productDTO.name());
        p.setBasePrice(productDTO.basePrice());
        for (String toppingName : productDTO.toppings()) {
            Topping topping = toppingRp.findByName(toppingName)
                    .orElseThrow(
                            () -> new BadRequestException("Topping with name '" + toppingName + "doesn't exist")
                    );
        }
        for (String toppingName : productDTO.toppings()) {
            assert toppingRp.findByName(toppingName).isPresent();
            Topping topping = toppingRp.findByName(toppingName).get();
            p.getToppings().add(topping);
        }

        Category category;

        if (isPresentCategory(productDTO.category())) {
            assert categoryRp.findByName(productDTO.category()).isPresent();
            category = categoryRp.findByName(productDTO.category()).get();
        } else {
            category = new Category(productDTO.category());
        }
        p.setCategory(category);
        p.setProductTotalAmount();
        p.setToppings(p.getToppings().stream()
                .peek(t -> t.setDescription(t.getName() + " (" + df.format(t.getPrice()) + "€)")).toList());

        return productRp.save(p);

    }

}
