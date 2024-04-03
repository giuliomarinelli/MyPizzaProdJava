package backendapp.myPizza.config;

import backendapp.myPizza.Models.entities.InternationalPrefix;
import backendapp.myPizza.repositories.PrefixRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.charset.Charset;

@Log4j2
//@Component
public class PrefixConfig implements CommandLineRunner {

    @Autowired
    private PrefixRepository prefixRp;

    @Override
    public void run(String... args) throws Exception {
        File prefixes = new File("src/main/resources/datasets/country-codes.csv");
        String[] rows = FileUtils.readFileToString(prefixes, Charset.defaultCharset()).split("\n");
        for (int i = 1; i < rows.length; i++) {
            String[] row = rows[i].split(",");
            for (String item : row) {
                if (!item.startsWith("\"+")) continue;
                String prefix = item.replaceAll("\"", "").trim();
                try {
                    prefixRp.save(new InternationalPrefix(prefix));
                    log.info("New prefix => " + prefix);
                } catch (Exception e) {
                    log.error("Error => " + e.getMessage());
                }
            }

        }
    }
}
