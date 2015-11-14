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
import java.util.Date;

/**
 *
 * @author Maxim Zaytsev
 */
public class Migration {

    private MySQLHelper oldFormatDB;
    private MySQLHelper newFormatDB;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static Integer allBlocks = 12;
    public static Integer totalBlock = 0;

    public Migration() {}
    
    public void executeMigration() {
        oldFormatDB = new MySQLHelper(SystemConstants.getOldFormatDatabaseConnectionsName());
        newFormatDB = new MySQLHelper(SystemConstants.getNewFormatDatabaseConnectionsName());
        this.startBlock("Groups");
        MigrationShopGroups shopGroups = new MigrationShopGroups(oldFormatDB, newFormatDB);
        shopGroups.migrationSQL();
        this.endBlock("Groups");
        this.startBlock("Groups Hierarchy");
        MigrationShopGroupsHierarchy shopGroupsHierarchy = new MigrationShopGroupsHierarchy(oldFormatDB, newFormatDB);
        shopGroupsHierarchy.migrationSQL();
        this.endBlock("Groups Hierarchy");
        this.startBlock("Prices Types");
        MigrationShopPricesTypes shopPricesTypes = new MigrationShopPricesTypes(oldFormatDB, newFormatDB);
        shopPricesTypes.migrationSQL();
        this.endBlock("Prices Types");
        this.startBlock("Items");
        MigrationShopItems shopItems = new MigrationShopItems(oldFormatDB, newFormatDB);
        shopItems.migrationSQL();
        this.endBlock("Items");
        this.startBlock("Items Prices");
        MigrationShopItemsPrices shopItemsPrices = new MigrationShopItemsPrices(oldFormatDB, newFormatDB);
        shopItemsPrices.migrationSQL();
        this.endBlock("Items Prices");
        this.startBlock("Properties");
        MigrationShopProperties shopProperties = new MigrationShopProperties(oldFormatDB, newFormatDB);
        shopProperties.migrationSQL();
        this.endBlock("Properties");
        this.startBlock("Measure");
        MigrationShopMeasure shopMeasure = new MigrationShopMeasure(oldFormatDB, newFormatDB);
        shopMeasure.migrationSQL();
        this.endBlock("Measure");
        this.startBlock("Properties Measure");
        MigrationShopPropertiesMeasure shopPropertiesMeasure = new MigrationShopPropertiesMeasure(oldFormatDB, newFormatDB);
        shopPropertiesMeasure.migrationSQL();
        this.endBlock("Properties Measure");
        this.startBlock("Measure Prefix");
        MigrationShopMeasurePrefix shopMeasurePrefix = new MigrationShopMeasurePrefix(oldFormatDB, newFormatDB);
        shopMeasurePrefix.migrationSQL();
        this.endBlock("Measure Prefix");
        this.startBlock("Measure Scaling");
        MigrationShopMeasureScaling shopMeasureScaling = new MigrationShopMeasureScaling(oldFormatDB, newFormatDB);
        shopMeasureScaling.migrationSQL();
        this.endBlock("Measure Scaling");
        this.startBlock("Properties In Groups");
        MigrationShopPropertiesInGroups propertiesInGroups = new MigrationShopPropertiesInGroups(oldFormatDB, newFormatDB);
        propertiesInGroups.migrationSQL();
        this.endBlock("Properties In Groups");
        this.startBlock("Properties Values");
        MigrationShopItemsPropertiesValues shopItemsPropertiesValues = new MigrationShopItemsPropertiesValues(oldFormatDB, newFormatDB);
        shopItemsPropertiesValues.migrationSQL();
        this.endBlock("Properties Values");
        this.oldFormatDB.close();
        this.newFormatDB.close();
    }
    
    public static String getThisBlock() {
        return Migration.totalBlock + "/" + Migration.allBlocks;
    }
    
    private void startBlock(String blockName) {
        this.totalBlock++;
        Date date = new Date();
        System.out.println(this.dateFormat.format(date) + " > Mirgation (" + Migration.getThisBlock() + ") " + blockName + " - START!");
    }
    
    private void endBlock(String blockName) {
        Date date = new Date();
        System.out.println(this.dateFormat.format(date) + " > Mirgation (" + Migration.getThisBlock() + ") " + blockName + " - DONE!");
        System.out.println();
    }
    
    
}
