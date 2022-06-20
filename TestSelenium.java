import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.managers.ChromeDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestSelenium {
    public WebDriverManager wdm;
    public WebDriver driver;
    public WebDriverWait wdwait;

    public void logIn(int x){
    wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("login_credentials")));
    String[] userNames = driver.findElement(By.id("login_credentials")).getText().split("\n");
    String password = driver.findElement(By.className("login_password")).getText().split("\n")[1];
    //System.out.println(userNames[x] + " " + password);
    driver.findElement(By.id("user-name")).sendKeys(userNames[x]);
    driver.findElement(By.id("password")).sendKeys(password);
    driver.findElement(By.id("login-button")).click();
    }

    //naredna metoda pomaze u sedmom zadatku
    public List<String> getItemNames(){
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("inventory_item_name")));
        List<WebElement> itemNames = driver.findElements(By.className("inventory_item_name"));
        List<String> names = new ArrayList<>();
        for (WebElement itemName: itemNames) if (itemName.getTagName().equals("div")) names.add(itemName.getText());
        return names;
    }

    @BeforeMethod
    public void setUp(){
     wdm = new ChromeDriverManager();
     wdm.setup();
     driver = new ChromeDriver();
     wdwait = new WebDriverWait(driver, Duration.ofSeconds(10));
     driver.manage().window().maximize();
    }

    @Test
    public void loginStandardUser() {
    driver.navigate().to("https://www.saucedemo.com/");
    logIn(1);
    wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("inventory_container")));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/inventory.html");
        Assert.assertNotEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/");
    }


    @Test
    public void loginLockedOutUser(){
        driver.navigate().to("https://www.saucedemo.com/");
        logIn(2);
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/");
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("error-button")));
        List<WebElement> h3s = driver.findElements(By.tagName("h3"));
        String s = "";
        for (WebElement h3:h3s) {
            if (h3.getAttribute("data-test").equals("error")) s = h3.getText();
            break;
        }
        Assert.assertEquals(s, "Epic sadface: Sorry, this user has been locked out.");
    }


    @Test
    public void addToCartSecondItem(){
    driver.navigate().to("https://www.saucedemo.com/");
    logIn(1);
    wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("btn_inventory")));
    List<WebElement> buttons = driver.findElements(By.className("btn_inventory"));
    buttons.get(1).click();
    //Provera da korpa nije prazna preko crvene ikonice na korpi gde treba da je neki broj veci od 0 kad ima neceg u njoj
    wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("shopping_cart_badge")));
    int num = Integer.parseInt(driver.findElement(By.className("shopping_cart_badge")).getText());
    Assert.assertEquals(num>0, true);

    }

    @Test
    public void addToCartSauceLabsFleeceJacket(){
        driver.navigate().to("https://www.saucedemo.com/");
        logIn(1);
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-to-cart-sauce-labs-fleece-jacket")));
        driver.findElement(By.id("add-to-cart-sauce-labs-fleece-jacket")).click();
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("shopping_cart_badge")));
        int num = Integer.parseInt(driver.findElement(By.className("shopping_cart_badge")).getText());
        Assert.assertEquals(num>0, true);
    }


    @Test
    public void removeFromCart() throws InterruptedException { driver.navigate().to("https://www.saucedemo.com/");
        logIn(1);
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("add-to-cart-sauce-labs-fleece-jacket")));
        driver.findElement(By.id("add-to-cart-sauce-labs-fleece-jacket")).click();
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("shopping_cart_badge")));
        driver.findElement(By.id("remove-sauce-labs-fleece-jacket")).click();
        Thread.sleep(2000);
        //provera da je korpa prazna tako sto nema broja na korpi
        List<WebElement> shoppingCartBadges = driver.findElements(By.className("shopping_cart_badge"));
        Assert.assertEquals(shoppingCartBadges.size(), 0);
    }

    @Test
    public void logout(){
        driver.navigate().to("https://www.saucedemo.com/");
        logIn(1);
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("react-burger-menu-btn")));
        driver.findElement(By.id("react-burger-menu-btn")).click();
        wdwait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout_sidebar_link")));
        driver.findElement(By.id("logout_sidebar_link")).click();
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.id("login-button")));
        Assert.assertTrue(driver.getCurrentUrl().equals("https://www.saucedemo.com/"));
        Assert.assertFalse(driver.getCurrentUrl().equals("https://www.saucedemo.com/inventory.html"));
        Assert.assertEquals(driver.findElements(By.id("login-button")).size() > 0, true);
    }


    @Test
    public void nameZToA(){
        driver.navigate().to("https://www.saucedemo.com/");
        logIn(1);
        List<String> list1 = getItemNames();
        Collections.reverse(list1);
        wdwait.until(ExpectedConditions.presenceOfElementLocated(By.className("product_sort_container")));
        Select sortBy = new Select(driver.findElement(By.className("product_sort_container")));
        sortBy.selectByValue("za");
        List<String> list2 = getItemNames();
        Assert.assertEquals(list1.size(), list2.size());
        for (int i = 0; i < list1.size(); i++) Assert.assertTrue(list1.get(i).equals(list2.get(i)));
        for (int i = 0; i < list1.size(); i++) System.out.println(list1.get(i) + " " + list2.get(i));
    }

    @AfterMethod
    public void tearDown() throws InterruptedException {
        Thread.sleep(5000);
        driver.quit();
    }

}
