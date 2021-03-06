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

import ShopMigrationDatabase.Helpers.MySQLHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxim Zaytsev
 */
public class MigrationShopMeasurePrefix {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final PreparedStatement updatePreparedStatement;
    private final PreparedStatement insertPreparedStatement;

    public MigrationShopMeasurePrefix(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.updatePreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopMeasurePrefix` SET `prefixS`=?,`factor`=?,"
                + "`reverseFactor`=? WHERE `prefixF`=?;");
        this.insertPreparedStatement = this.newFormatDB.preparedStatement(
                "INSERT INTO `ShopMeasurePrefix`(`prefixF`, `prefixS`, "
                + "`factor`, `reverseFactor`) VALUES (?,?,?,?);");
    }

    public void migrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `prefixF`, `prefixS`, `factor`, `reverseFactor` FROM `ShopItemsPropertiesMeasurePrefix`;");
        try {
            while (oldFormatRS.next()) {
                String prefixF = oldFormatRS.getString("prefixF");
                String prefixS = oldFormatRS.getString("prefixS");
                Float factor = oldFormatRS.getFloat("factor");
                Float reverseFactor = oldFormatRS.getFloat("reverseFactor");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`prefixF`) as amount FROM `ShopMeasurePrefix` WHERE `prefixF`='" + prefixF + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    this.sqlUpdate(prefixF, prefixS, factor, reverseFactor);
                } else {
                    this.sqlInsert(prefixF, prefixS, factor, reverseFactor);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlUpdate(String prefixF, String prefixS, Float factor, Float reverseFactor) {
        System.out.println(Migration.getThisBlock() + " Update Measure Prefix " + prefixF + " (" + prefixS + ")");
        try {
            this.updatePreparedStatement.setString(1, prefixS);
            this.updatePreparedStatement.setFloat(2, factor);
            this.updatePreparedStatement.setFloat(3, reverseFactor);
            this.updatePreparedStatement.setString(4, prefixF);
            this.updatePreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlInsert(String prefixF, String prefixS, Float factor, Float reverseFactor) {
        System.out.println(Migration.getThisBlock() + " Insert Measure Prefix " + prefixF + " (" + prefixS + ")");
        try {
            this.insertPreparedStatement.setString(1, prefixF);
            this.insertPreparedStatement.setString(2, prefixS);
            this.insertPreparedStatement.setFloat(3, factor);
            this.insertPreparedStatement.setFloat(4, reverseFactor);
            this.insertPreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
