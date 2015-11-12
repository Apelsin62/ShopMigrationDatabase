/*
 * НЕ ИЗМЕНЯТЬ И НЕ УДАЛЯТЬ АВТОРСКИЕ ПРАВА И ЗАГОЛОВОК ФАЙЛА
 * 
 * Копирайт © 2010-2016, CompuProject и/или дочерние компании.
 * Все права защищены.
 * 
 * ShopMigrationDatabase это программное обеспечение предоставленное и разработанное 
 * CompuProject в рамках проекта ApelsinShop без каких либо сторонних изменений.
 * 
 * Распространение, использование исходного кода в любой форме и/или его 
 * модификация разрешается при условии, что выполняются следующие условия:
 * 
 * 1. При распространении исходного кода должно оставатсья указанное выше 
 *    уведомление об авторских правах, этот список условий и последующий 
 *    отказ от гарантий.
 * 
 * 2. При изменении исходного кода должно оставатсья указанное выше 
 *    уведомление об авторских правах, этот список условий, последующий 
 *    отказ от гарантий и пометка о сделанных изменениях.
 * 
 * 3. Распространение и/или изменение исходного кода должно происходить
 *    на условиях Стандартной общественной лицензии GNU в том виде, в каком 
 *    она была опубликована Фондом свободного программного обеспечения;
 *    либо лицензии версии 3, либо (по вашему выбору) любой более поздней
 *    версии. Вы должны были получить копию Стандартной общественной 
 *    лицензии GNU вместе с этой программой. Если это не так, см. 
 *    <http://www.gnu.org/licenses/>.
 * 
 * ShopMigrationDatabase распространяется в надежде, что она будет полезной,
 * но БЕЗО ВСЯКИХ ГАРАНТИЙ; даже без неявной гарантии ТОВАРНОГО ВИДА
 * или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Подробнее см. в Стандартной
 * общественной лицензии GNU.
 * 
 * НИ ПРИ КАКИХ УСЛОВИЯХ ПРОЕКТ, ЕГО УЧАСТНИКИ ИЛИ CompuProject НЕ 
 * НЕСУТ ОТВЕТСТВЕННОСТИ ЗА КАКИЕ ЛИБО ПРЯМЫЕ, КОСВЕННЫЕ, СЛУЧАЙНЫЕ, 
 * ОСОБЫЕ, ШТРАФНЫЕ ИЛИ КАКИЕ ЛИБО ДРУГИЕ УБЫТКИ (ВКЛЮЧАЯ, НО НЕ 
 * ОГРАНИЧИВАЯСЬ ПРИОБРЕТЕНИЕМ ИЛИ ЗАМЕНОЙ ТОВАРОВ И УСЛУГ; ПОТЕРЕЙ 
 * ДАННЫХ ИЛИ ПРИБЫЛИ; ПРИОСТАНОВЛЕНИЕ БИЗНЕСА). 
 * 
 * ИСПОЛЬЗОВАНИЕ ДАННОГО ИСХОДНОГО КОДА ОЗНАЧАЕТ, ЧТО ВЫ БЫЛИ ОЗНАКОЛМЛЕНЫ
 * СО ВСЕМИ ПРАВАМИ, СТАНДАРТАМИ И УСЛОВИЯМИ, УКАЗАННЫМИ ВЫШЕ, СОГЛАСНЫ С НИМИ
 * И ОБЯЗУЕТЕСЬ ИХ СОБЛЮДАТЬ.
 * 
 * ЕСЛИ ВЫ НЕ СОГЛАСНЫ С ВЫШЕУКАЗАННЫМИ ПРАВАМИ, СТАНДАРТАМИ И УСЛОВИЯМИ, 
 * ТО ВЫ МОЖЕТЕ ОТКАЗАТЬСЯ ОТ ИСПОЛЬЗОВАНИЯ ДАННОГО ИСХОДНОГО КОДА.
 * 
 */
package ShopMigrationDatabase.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Maxim Zaytsev
 */
public class MigrationHelper {

    private static Map<String, String> propertiesId = new HashMap<>();
    private static ArrayList<String> pricesTypes = new ArrayList<>();
    private static String defaultPricesTypes;
    private static ArrayList<String> itemsId = new ArrayList<>();
    
    public static void setItemsId(ArrayList<String> items) {
        MigrationHelper.itemsId = items;
    }
    
    public static ArrayList<String> getItemsId() {
        return MigrationHelper.itemsId;
    }
    
    public static void setPricesTypes(ArrayList<String> pricesTypes, String defaultPricesTypes) {
        MigrationHelper.pricesTypes = pricesTypes;
        MigrationHelper.defaultPricesTypes = defaultPricesTypes;
    }
    
    public static ArrayList<String> getPricesTypes() {
        return MigrationHelper.pricesTypes;
    }
    
    public static String getDefaultPricesTypes() {
        return MigrationHelper.defaultPricesTypes;
    }


    public static void setPropertiesId(Map<String, String> propertiesId) {
        MigrationHelper.propertiesId = propertiesId;
    }

    public static Map<String, String> getPropertiesId(Map<String, String> propertiesId) {
        return MigrationHelper.propertiesId;
    }

    public static String getPropertyId(String id) {
        String rezult = id;
        if (MigrationHelper.propertiesId.get(id) != null) {
            rezult = MigrationHelper.propertiesId.get(id);
        }
        return rezult;
    }

    public static String getFilterType(String type) {
        String newType;
        switch (type) {
            case "bool":
                newType = "bool";
                break;
            case "groupSelect":
                newType = "groupSelect";
                break;
            case "intRange":
                newType = "range";
                break;
            case "select":
                newType = "select";
                break;
            case "text":
                newType = "text";
                break;
            default:
                newType = "none";
                break;
        }
        return newType;
    }

    public static String getValueType(String type) {
        String newType;
        switch (type) {
            case "bool":
                newType = "bool";
                break;
            case "float":
                newType = "float";
                break;
            case "int":
                newType = "int";
                break;
            case "text":
                newType = "text";
                break;
            default:
                newType = "varchar";
                break;
        }
        return newType;
    }
}
