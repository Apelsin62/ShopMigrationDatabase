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
package ShopMigrationDatabase.Migration;

import ShopMigrationDatabase.General.SystemConstants;
import ShopMigrationDatabase.Helpers.MySQLHelper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Maxim Zaytsev
 */
public class Migration {

    private final MySQLHelper oldFormatDB = new MySQLHelper(SystemConstants.getOldFormatDatabaseConnectionsName());
    private final MySQLHelper newFormatDB = new MySQLHelper(SystemConstants.getNewFormatDatabaseConnectionsName());
    private final Integer allBlocks = 12;

    public Migration() {
        MigrationShopGroups shopGroups = new MigrationShopGroups(oldFormatDB, newFormatDB);
        this.executeSqlList(shopGroups.getSqlList(), "ShopGroups", 1);
        MigrationShopGroupsHierarchy shopGroupsHierarchy = new MigrationShopGroupsHierarchy(oldFormatDB, newFormatDB);
        this.executeSqlList(shopGroupsHierarchy.getSqlList(), "ShopGroupsHierarchy", 2);
        MigrationShopPricesTypes shopPricesTypes = new MigrationShopPricesTypes(oldFormatDB, newFormatDB);
        this.executeSqlList(shopPricesTypes.getSqlList(), "ShopPricesTypes", 3);
        MigrationShopItems shopItems = new MigrationShopItems(oldFormatDB, newFormatDB);
        this.executeSqlList(shopItems.getSqlList(), "ShopItems", 4);
        MigrationShopItemsPrices shopItemsPrices = new MigrationShopItemsPrices(oldFormatDB, newFormatDB);
        this.executeSqlList(shopItemsPrices.getSqlList(), "ShopItemsPrices", 5);
        MigrationShopProperties shopProperties = new MigrationShopProperties(oldFormatDB, newFormatDB);
        this.executeSqlList(shopProperties.getSqlList(), "ShopProperties", 6);
        MigrationShopMeasure shopMeasure = new MigrationShopMeasure(oldFormatDB, newFormatDB);
        this.executeSqlList(shopMeasure.getSqlList(), "ShopMeasure", 7);
        MigrationShopPropertiesMeasure shopPropertiesMeasure = new MigrationShopPropertiesMeasure(oldFormatDB, newFormatDB);
        this.executeSqlList(shopPropertiesMeasure.getSqlList(), "ShopPropertiesMeasure", 8);
        MigrationShopMeasurePrefix shopMeasurePrefix = new MigrationShopMeasurePrefix(oldFormatDB, newFormatDB);
        this.executeSqlList(shopMeasurePrefix.getSqlList(), "ShopMeasurePrefix", 9);
        MigrationShopMeasureScaling shopMeasureScaling = new MigrationShopMeasureScaling(oldFormatDB, newFormatDB);
        this.executeSqlList(shopMeasureScaling.getSqlList(), "ShopMeasureScaling", 10);
        MigrationShopPropertiesInGroups propertiesInGroups = new MigrationShopPropertiesInGroups(oldFormatDB, newFormatDB);
        this.executeSqlList(propertiesInGroups.getSqlList(), "PropertiesInGroups", 11);
        MigrationShopItemsPropertiesValues shopItemsPropertiesValues = new MigrationShopItemsPropertiesValues(oldFormatDB, newFormatDB);
        this.executeSqlList(shopItemsPropertiesValues.getSqlList(), "ShopItemsPropertiesValues", 12);
        this.oldFormatDB.close();
        this.newFormatDB.close();
    }

    private void executeSqlList(ArrayList<String> sqlList, String blockName, Integer totalBlock) {
        int all = sqlList.size();
        int total = 0;
        int old = -1;
        old = this.progressBar(blockName, all, total++, old, totalBlock);
        for (String sql : sqlList) {
            this.newFormatDB.executeUpdate(sql);
            old = this.progressBar(blockName, all, total++, old, totalBlock);
        }
        System.out.println(blockName + " done!");
        System.out.println();
    }

    private int progressBar(String name, int all, int total, int old, int totalBlock) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
        int data;
        data = (int) ((float) total / (float) all * 100);
        if (data > old) {
            Date date = new Date();
            System.out.println(name + " (" + totalBlock + "/" + this.allBlocks + ") [" + dateFormat.format(date) + "] " + (int) data + "%");
        }
        return data;
    }
}
