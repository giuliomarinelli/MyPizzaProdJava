package backendapp.myPizza.services;

import backendapp.myPizza.Models.entities.InternationalPrefix;
import backendapp.myPizza.repositories.PrefixRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrefixService {

    @Autowired
    private PrefixRepository prefixRp;

    public List<InternationalPrefix> findAll() {
        return prefixRp.findAllOrderAsc();
    }
}
