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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 *
 * @author Maxim Zaytsev
 */
public class IdGenerator {

    private static String dict = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
    
    
    public static String generageId(String prefix, String postfix) {
        Random random = new Random(System.currentTimeMillis());
        GregorianCalendar today = new GregorianCalendar();
        Integer day = today.get(Calendar.DAY_OF_MONTH);
        Integer day1 = day/10;
        Integer day2 = day - (day1 * 10);
        Integer month = today.get(Calendar.MONTH);
        Integer month1 = month/10;
        Integer month2 = month - (month1 * 10);
        Integer year = today.get(Calendar.YEAR);
        Integer yearT1 = year/100;
        Integer yearT2 = year - (yearT1 * 100);
        Integer year1 = yearT1/10;
        Integer year2 = yearT1 - (year1 * 10);
        Integer year3 = yearT2/10;
        Integer year4 = yearT2 - (year3 * 10);
        Integer hour = today.get(Calendar.HOUR_OF_DAY);
        Integer minute = today.get(Calendar.MINUTE);
        Integer minute1 = minute/10;
        Integer minute2 = minute - (minute1 * 10);
        Integer second = today.get(Calendar.SECOND);
        Integer second1 = second/10;
        Integer second2 = second - (second1 * 10);
        Integer millisecond = today.get(Calendar.MILLISECOND);
        Integer millisecond1 = millisecond/100;
        Integer millisecondTemp = (millisecond - ((millisecond/100) * 100));
        Integer millisecond2 = millisecondTemp / 10;
        Integer millisecond3 = (millisecondTemp - ((millisecondTemp/10) * 10));
        ArrayList<Integer> digitalSet = new ArrayList<>();
        digitalSet.add(IdGenerator.getAvalibalDigital(day1));
        digitalSet.add(IdGenerator.getAvalibalDigital(day2));
        digitalSet.add(IdGenerator.getAvalibalDigital(month1));
        digitalSet.add(IdGenerator.getAvalibalDigital(month2));
        digitalSet.add(IdGenerator.getAvalibalDigital(year1));
        digitalSet.add(IdGenerator.getAvalibalDigital(year2));
        digitalSet.add(IdGenerator.getAvalibalDigital(year3));
        digitalSet.add(IdGenerator.getAvalibalDigital(year4));
        digitalSet.add(IdGenerator.getAvalibalDigital(hour));
        digitalSet.add(IdGenerator.getAvalibalDigital(minute1));
        digitalSet.add(IdGenerator.getAvalibalDigital(minute2));
        digitalSet.add(IdGenerator.getAvalibalDigital(second1));
        digitalSet.add(IdGenerator.getAvalibalDigital(second2));
        digitalSet.add(IdGenerator.getAvalibalDigital(millisecond1));
        digitalSet.add(IdGenerator.getAvalibalDigital(millisecond2));
        digitalSet.add(IdGenerator.getAvalibalDigital(millisecond3));
        String code;
        if(prefix != null && prefix != "") {
            code = prefix.concat("-");
        } else {
            code = "";
        }
        Integer counter = 0;
        for (Integer digital : digitalSet) {
            counter++;
            code = code.concat(String.format("%c", IdGenerator.dict.charAt(digital)));
            Integer randomDigital1 = random.nextInt(IdGenerator.dict.length());
            Integer randomDigital2 = random.nextInt(IdGenerator.dict.length());
            code = code.concat(String.format("%c", IdGenerator.dict.charAt(randomDigital1)));
            if(counter == 6) {
                code = code.concat(String.format("%c", IdGenerator.dict.charAt(randomDigital2)));
                code = code.concat("-");
                counter = 0;
            }
        }
        if(postfix != null && postfix != "") {
            code = code.concat("-");
            code = code.concat(postfix);
        }
        return code;
    }
    
    public static String generageId(String prefix) {
        return IdGenerator.generageId(prefix,null);
    }
    
    public static String generageId() {
        return IdGenerator.generageId(null,null);
    }
    
    private static Integer getAvalibalDigital(Integer digital) {
        while (digital >= IdGenerator.dict.length()) {
            digital = digital - IdGenerator.dict.length();
        }
        return digital;
    }
}
