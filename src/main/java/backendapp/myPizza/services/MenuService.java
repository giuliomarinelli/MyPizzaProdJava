package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.Menu;
import backendapp.myPizza.Models.entities.Product;
import backendapp.myPizza.Models.resDTO.ConfirmRes;
import backendapp.myPizza.exceptions.BadRequestException;
import backendapp.myPizza.repositories.MenuRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRp;

    public Page<Menu> getMenu(Pageable pageable) {
        return menuRp.findAll(pageable).map(m -> {
            if (m.getItem() instanceof Product) ((Product) m.getItem()).setProductTotalAmount();
            return m;
        });
    }

    public ConfirmRes saveMenu(List<UUID> menuIds) throws BadRequestException {
        List<Menu> newMenuList = new ArrayList<>();
        for (UUID menuId : menuIds) {
            Menu oldMenu = menuRp.findById(menuId).orElseThrow(
                    () -> new BadRequestException("Menu instance with id='" + menuId + "' doesn't exist")
            );
            newMenuList.add(new Menu(oldMenu.getItem()));
        }
        menuRp.deleteAll();
        menuRp.saveAll(newMenuList);

        return new ConfirmRes("Menu updated successfully", HttpStatus.OK);
    }
}
