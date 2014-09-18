/**
 * Created by raoul-gabrielurma on 14/01/2014.
 */


import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

import org.junit.*;

import static java.util.stream.Collectors.*;
import static org.junit.Assert.*;

import static java.util.stream.Collectors.*;

public class PuttingInToPracticeTest {

    List<Transaction> transactions = new ArrayList<>();

    @Before
    public void setUp() throws Exception {

        Trader raoul = new Trader("Raoul", "Cambridge");
        Trader mario = new Trader("Mario","Milan");
        Trader alan = new Trader("Alan","Cambridge");
        Trader brian = new Trader("Brian","Cambridge");

        transactions = Arrays.asList(
                new Transaction(brian, 2011, 300),
                new Transaction(raoul, 2012, 1000),
                new Transaction(raoul, 2011, 400),
                new Transaction(mario, 2012, 710),
                new Transaction(mario, 2012, 700),
                new Transaction(alan, 2012, 950)
        );
        
        
        
    }

    /*

1000
{Trader:Brian in Cambridge, year: 2011, value:300}
{Trader:Raoul in Cambridge, year: 2012, value:1000}
     */

    @Test
    public void testStream1(){
        // Find all transactions from year 2011 and sort them by value (small to high).
        List<Transaction> transactionsYear2011 =  
        		transactions.stream()
        			.filter(t -> t.getYear() == 2011)
        			.sorted(Comparator.comparing(Transaction::getValue))
        			.collect(Collectors.toList());

        assertEquals(300, transactionsYear2011.get(0).getValue());
        assertEquals(2011, transactionsYear2011.get(0).getYear());

        assertEquals(400, transactionsYear2011.get(1).getValue());
        assertEquals(2011, transactionsYear2011.get(1).getYear());

        assertEquals(2, transactionsYear2011.size());
    }

    @Test
    public void testStream2(){
        // What are all the unique cities where the traders work?
        List<String> cities = transactions.stream()
        		.map(Transaction::getTrader)
        		.map(Trader::getCity)
        		.distinct()
        		.collect(Collectors.toList());

        assertEquals(2, cities.size());
        assertTrue(cities.contains("Cambridge"));
        assertTrue(cities.contains("Milan"));

    }

    @Test
    public void testStream3(){
        // Find all traders from Cambridge and sort them by name.
        List<Trader> traders = transactions.stream()
        		.map(Transaction::getTrader)
        		.filter(t -> t.getCity().equals("Cambridge"))
        		.sorted(Comparator.comparing(Trader::getName))
        		.distinct()
        		.collect(Collectors.toList());
        		
        assertEquals(3, traders.size());
        assertEquals("Alan",traders.get(0).getName());
        assertEquals("Brian",traders.get(1).getName());
        assertEquals("Raoul", traders.get(2).getName());
    }

    @Test
    public void testStream4(){
        // Return a string of all traders’ names sorted alphabetically.
        String result = transactions.stream()
        		.map(t -> t.getTrader().getName())
        		.distinct()
        		.sorted()
        		.collect(Collectors.joining());

        assertEquals("AlanBrianMarioRaoul", result);
    }

    @Test
    public void testStream5(){
        // Are there any trader based in Milan?
        boolean milan = transactions.stream()
        		.anyMatch(t -> t.getTrader().getCity().equals("Milan"));
        		
        assertEquals(true, milan);
    }

    @Test
    public void testStream6(){
        // Update all transactions so that the traders from Milan are set to Cambridge.
    	transactions.stream()
    		.filter(t -> t.getTrader().getCity().equals("Milan"))
    		.forEach(t -> t.getTrader().SetCity("Cambridge"));

        assertTrue(transactions.stream().allMatch(t -> "Cambridge".equals(t.getTrader().getCity())));

    }

    @Test
    public void testStream7(){
        // What's the highest value in all the transactions?
        int highestValue = transactions.stream()
        		.mapToInt(Transaction::getValue)
        		.max().getAsInt();

        assertEquals(1000, highestValue);

    }

    @Test
    public void testStream8(){
        // What's the transaction with smallest value?
        Transaction smallestTransaction = transactions.stream()
        		.sorted(Comparator.comparing(Transaction::getValue))
        		.findFirst().get();

        assertEquals(transactions.get(0), smallestTransaction);
    }

    @Test
    public void testStream9(){
        // Group all Traders by City
        Map<String, List<Trader>> tradersByCity = transactions.stream()
        		.map(Transaction::getTrader)
        		.distinct()
        		.collect(groupingBy(Trader::getCity)); 

        assertEquals(1, tradersByCity.get("Milan").size());
        assertEquals(3, tradersByCity.get("Cambridge").size());
    }

    @Test
    public void testStream10() {
        // Create a Map<String, Integer> that maps each Trader's name with its highest Transaction value
        Map<String, Integer> mapNameToHighestValue = 
        	transactions.stream()
        		.collect(groupingBy(tx -> tx.getTrader().getName(), 
        			collectingAndThen(
        				maxBy(comparing(Transaction::getValue)),
        				opt -> opt.map(Transaction::getValue).get())
        			));

        assertEquals(1000, (int) mapNameToHighestValue.get("Raoul"));
        assertEquals(300, (int) mapNameToHighestValue.get("Brian"));
        assertEquals(950, (int) mapNameToHighestValue.get("Alan"));
        assertEquals(710, (int) mapNameToHighestValue.get("Mario"));
    }

    @Test
    public void testStream11(){
        // Create a Map<String, Integer> that maps each Trader's name with the sum of all its Transactions’ values
        Map<String, Integer> mapNameToSumValue =
        	transactions.stream()
        		.collect(groupingBy(tx -> tx.getTrader().getName(), 
        				summingInt((Transaction tx) -> tx.getValue())));

        assertEquals(1400, (int) mapNameToSumValue.get("Raoul"));
        assertEquals(300, (int) mapNameToSumValue.get("Brian"));
        assertEquals(950, (int) mapNameToSumValue.get("Alan"));
        assertEquals(1410, (int) mapNameToSumValue.get("Mario"));
    }

    @Test
    public void testStream12(){
        // Create a Map<Integer, Integer> that maps each year with the highest transaction value of that year
        Map<Integer, Integer> yearToHighestValue = //new HashMap<>();
        		transactions.stream()
        			.collect(groupingBy(
        				Transaction::getYear,
        				collectingAndThen(
        					maxBy(comparing(Transaction::getValue)), 
        					opttx -> opttx.map(Transaction::getValue).get())
        		));

        assertEquals(1000, (int) yearToHighestValue.get(2012));
        assertEquals(400, (int) yearToHighestValue.get(2011));
    }

    @Test
    public void testStream13(){
        // What's the transaction with highest value? (using Collectors.maxBy)
        Transaction highestTransaction = 
        	transactions.stream()
        		.collect(
        				collectingAndThen(
        						maxBy(comparing(Transaction::getValue)),
        						Optional::get)
        		);


        assertEquals(transactions.get(1), highestTransaction);
    }


}
