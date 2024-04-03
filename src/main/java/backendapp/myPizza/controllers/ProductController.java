package backendapp.myPizza.controllers;

import backendapp.myPizza.Models.entities.Menu;
import backendapp.myPizza.Models.entities.Product;
import backendapp.myPizza.Models.entities.Topping;
import backendapp.myPizza.Models.enums.ToppingType;
import backendapp.myPizza.Models.reqDTO.*;
import backendapp.myPizza.Models.resDTO.CategoriesRes;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.Models.resDTO.ProductNamesRes;
import backendapp.myPizza.Models.resDTO.ToppingsRes;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.exceptions.NotFoundException;
import backendapp.myPizza.exceptions.Validation;
import backendapp.myPizza.services.MenuService;
import backendapp.myPizza.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAuthority('ADMIN')")
public class ProductController {

    @Autowired
    private ProductService productSvc;

    @Autowired
    private MenuService menuSvc;

    // -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.
    // Toppings

    @GetMapping("/toppings")
    public ToppingsRes getAllToppings(@RequestParam(required = false) ToppingType type) {
        if (type.equals(ToppingType.EXTRA))
            return new ToppingsRes(productSvc.findAllExtras());
        else if (type.equals(ToppingType.TOPPING))
            return new ToppingsRes(productSvc.findAllToppingToppings());

        return new ToppingsRes(productSvc.findAllToppings());


    }

    @PostMapping("/toppings")
    public Topping addTopping(@RequestBody @Validated ToppingDTO toppingDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return productSvc.addTopping(toppingDTO);
    }

    @PutMapping("/toppings/{name}")
    public Topping updateToppingByName(@PathVariable String name, @RequestBody @Validated ToppingDTO toppingDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return productSvc.updateToppingByName(name, toppingDTO);
    }

    @DeleteMapping("/toppings/{name}")
    public ConfirmRes deleteToppingByName(@PathVariable String name) throws BadRequestException {
        return productSvc.deleteToppingByName(name);
    }

// -.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.-.
// Products

    @GetMapping("/products")
    public Page<Product> getAllProducts(Pageable pageable) {
        return productSvc.getAllProducts(pageable);
    }

    @GetMapping("/products/product-names")
    public ProductNamesRes getProductNames() {
        return new ProductNamesRes(productSvc.getProductNames());
    }

    @GetMapping("/products/categories")
    public CategoriesRes getCategories() {
        return new CategoriesRes(productSvc.getAllCategoryNames());
    }

    @PostMapping("/products")
    public Product addProduct(@RequestBody @Validated ProductDTO productDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return productSvc.addProduct(productDTO);
    }

    @PostMapping("/products/add-many")
    public List<Product> addManyProducts(@RequestBody @Validated ManyProductsPostDTO manyProductsPostDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return productSvc.addManyProducts(manyProductsPostDTO);
    }

    @PutMapping("/products/{name}")
    public Product updateProduct(@PathVariable String name, @RequestBody @Validated ProductDTO productDTO, BindingResult validation) throws BadRequestException, NotFoundException {
        Validation.validate(validation);
        return productSvc.updateProductByName(name, productDTO);
    }

    @DeleteMapping("/products/{name}")
    public ConfirmRes deleteProduct(@PathVariable String name) throws BadRequestException {
        return productSvc.deleteProductByName(name);
    }

    @PostMapping("/set-menu")
    public ConfirmRes setMenu(@RequestBody @Validated MenuDTO menuDTO, BindingResult validation) throws BadRequestException {
        Validation.validate(validation);
        return menuSvc.saveMenu(menuDTO.menuIds());
    }


}