package backendapp.myPizza.config;

import backendapp.myPizza.Models.entities.Address;
import backendapp.myPizza.Models.entities.City;
import backendapp.myPizza.repositories.CityRepository;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

//@Component
public class CityConfig implements CommandLineRunner {

    @Autowired
    private CityRepository cityRp;

    public static final Logger logger = LoggerFactory.getLogger(CityConfig.class);


    private String fixAccentInWord(String word) {
        if (word.endsWith("'")) {
            int ind = word.indexOf("'");
            System.out.println(word.charAt(word.length() - 2));
            switch (word.charAt(word.length() - 2)) {
                case 'a' -> word = word.substring(0, ind - 1) + "à";
                case 'e' -> word = word.substring(0, ind - 1) + "è";
                case 'i' -> word = word.substring(0, ind - 1) + "ì";
                case 'o' -> word = word.substring(0, ind - 1) + "ò";
                case 'u' -> word = word.substring(0, ind - 1) + "ù";

            }
        }
        return word;
    }


    private String fixAccentAndCaseInName(String name) {
        String res = "";
        if (name.contains("\\")) {
            String[] parts = name.split(" ");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = fixAccentInWord(parts[i].charAt(0) + parts[i].substring(1).toLowerCase());
            }
            return String.join(" ", parts);
        } else if (name.contains("-")) {
            String[] parts = name.split("-");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = fixAccentInWord(parts[i].charAt(0) + parts[i].substring(1).toLowerCase());
            }
            return String.join("-", parts);
        }
        return fixAccentInWord(name.charAt(0) + name.substring(1).toLowerCase());
    }


    @Override
    public void run(String... args) throws Exception {
        File provinceItaliane = new File("src/main/resources/datasets/province-italiane.csv");
        File comuniItaliani = new File("src/main/resources/datasets/comuni-italiani.csv");
        try {
            String provinceItalianeContent = FileUtils.readFileToString(provinceItaliane, Charset.defaultCharset());
            String comuniItalianiContent = FileUtils.readFileToString(comuniItaliani, Charset.defaultCharset());
            String[] pRows = provinceItalianeContent.split("\n");
            String[] cRows = comuniItalianiContent.split("\n");
            for (int i = 1; i < cRows.length; i++) {
                cRows[i] = cRows[i].replace(";;", ";");
                logger.info(cRows[i]);
                String[] cRow = cRows[i].split(";");
                String name = fixAccentAndCaseInName(cRow[1]);
                String provinceCode = cRow[2];
                String provinceName = "";
                String region = "";
                for (int j = 1; j < pRows.length; j++) {
                    String[] pRow = pRows[j].split(";");
                    if (pRow[0].equals(provinceCode)) {
                        provinceName = pRow[1];
                        region = pRow[2];
                        break;
                    }
                }
                cityRp.save(new City(i, name, provinceCode, provinceName, region));
                logger.info("Saved city => " + name);
            }


        } catch (
                IOException e) {
            logger.error("Error => " + e.getMessage());
        }

    }
}
