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

import ShopMigrationDatabase.General.Configuration;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxim Zaytsev
 */
public class MySQLHelper {

    private final String jdbcClassName;
    private final String jdbs;
    private final String user;
    private final String password;
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    private PreparedStatement pstmt;

    /**
     * На вход конструктора поступает название соединения с базой, на основе
     * которого из файла конфигурации определяются значения для подключения к
     * MySQL базе.
     *
     * @param connectionsName - название соединения с базой
     */
    public MySQLHelper(String connectionsName) {
        Configuration configuration = Configuration.getInstance();
        this.jdbcClassName = "com.mysql.jdbc.Driver";
        this.jdbs = configuration.getMySQLConnectionsData(connectionsName, "jdbc");
        this.user = configuration.getMySQLConnectionsData(connectionsName, "user");
        this.password = configuration.getMySQLConnectionsData(connectionsName, "password");
        createConn();
    }

    /**
     * Данный метод возвращает Connection базы данных
     *
     * @return Connection
     */
    private void createConn() {
        try {
            Class.forName(this.jdbcClassName);
            try {
                this.conn = DriverManager.getConnection(this.jdbs, this.user, this.password);
            } catch (SQLException ex) {
                Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Выполнение запроса к базе с ожидаемым ответом в виде набора данных
     *
     * @param sql - SQL запрос к базе данных
     * @return ResultSet - ответ от базы в виде набора данных
     */
    public ResultSet executeQuery(String sql) {
        this.rs = null;
        try {
            this.stmt = this.conn.createStatement();
            this.rs = this.stmt.executeQuery(sql);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.rs;
    }

    /**
     * Выполнение запроса к базе без ответа от базы, например запрос на вставку,
     * удаление или обновление данных в базе.
     *
     * @param sql - SQL запрос к базе данных
     * @return - либо количество затронутых в ходе изменения строк, либо 0 в
     * случае если выполненный SQL запрос не возвращает ничего.
     */
    public int executeUpdate(String sql) {
        int i = 0;
        this.pstmt = null;
        try {
            this.pstmt = this.conn.prepareStatement(sql);
            i = this.pstmt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return i;
    }

    /**
     *
     * @param sql
     * @return PreparedStatement
     */
    public PreparedStatement preparedStatement(String sql) {
        this.pstmt = null;
        try {
            this.pstmt = this.conn.prepareStatement(sql);
        } catch (SQLException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this.pstmt;
    }

    /**
     * Закрытие соединения с базой Вызывается в ручную
     */
    public void close() {
        if (this.conn != null) {
            try {
                this.conn.close();
            } catch (SQLException ex) {
                Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Устанавливает значение для AutoCommit
     *
     * @param b - булевое значение
     */
    public void setAutoCommit(Boolean b) {
        if (b) {
            try {
                this.conn.commit();
            } catch (SQLException ex) {
                Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                this.conn.rollback();
            } catch (SQLException ex) {
                Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Выполнить commit в ручную
     */
    public void commit() {
        try {
            this.conn.commit();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("");
    }

    /**
     * Выполнить rollback в ручную
     */
    public void rollback() {
        try {
            this.conn.rollback();
        } catch (SQLException ex) {
            Logger.getLogger(MySQLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("");
    }
}
