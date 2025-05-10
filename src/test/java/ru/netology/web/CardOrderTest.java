package ru.netology.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {

    private WebDriver driver;

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage", "--no-sandbox");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    // 1. Успешная отправка
    @Test
    void shouldSubmitRequestIfDataValid() {
        driver.findElement(By.cssSelector("[data-test-id=name] input"))
                .sendKeys("Злата Санникова");
        driver.findElement(By.cssSelector("[data-test-id=phone] input"))
                .sendKeys("+71234567890");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test-id=order-success]")).isDisplayed());
    }

    // 2. Пустое имя
    @Test
    void shouldShowErrorIfNameEmpty() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+71234567890");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        String classes = driver.findElement(By.cssSelector("[data-test-id=name]")).getAttribute("class");
        assertTrue(classes.contains("input_invalid"));
    }

    // 3. Ошибочное имя (латиница)
    @Test
    void shouldShowErrorIfNameWithLatinLetters() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("John Doe");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+71234567890");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        String classes = driver.findElement(By.cssSelector("[data-test-id=name]")).getAttribute("class");
        assertTrue(classes.contains("input_invalid"));
    }

    // 4. Пустой телефон
    @Test
    void shouldShowErrorIfPhoneEmpty() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        String classes = driver.findElement(By.cssSelector("[data-test-id=phone]")).getAttribute("class");
        assertTrue(classes.contains("input_invalid"));
    }

    // 5. Номер без плюса
    @Test
    void shouldShowErrorIfPhoneWithoutPlus() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("81234567890");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        String classes = driver.findElement(By.cssSelector("[data-test-id=phone]")).getAttribute("class");
        assertTrue(classes.contains("input_invalid"));
    }

    // 6. Согласие не отмечено
    @Test
    void shouldShowErrorIfAgreementNotChecked() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Иван Иванов");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+71234567890");
        // не кликаем по чекбоксу
        driver.findElement(By.tagName("button")).click();
        WebElement error = driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid"));
        assertTrue(error.isDisplayed());
    }

    // 7. Длинное имя (+ дефис)
    @Test
    void shouldAcceptLongHyphenatedName() {
        String longName = "Анна-Мария-Петрова-Смирнова-Лукьянова";
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys(longName);
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79876543210");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        assertTrue(driver.findElement(By.cssSelector("[data-test-id=order-success]")).isDisplayed());
    }

    // 8. Телефон с пробелами и дефисами
    @Test
    void shouldShowErrorIfPhoneWithSeparators() {
        driver.findElement(By.cssSelector("[data-test-id=name] input"))
                .sendKeys("Петр Петров");
        driver.findElement(By.cssSelector("[data-test-id=phone] input"))
                .sendKeys("+7 123-456-78-90");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.tagName("button")).click();
        String classes = driver.findElement(By.cssSelector("[data-test-id=phone]"))
                .getAttribute("class");
        assertTrue(classes.contains("input_invalid"),
                "Ожидали, что номер с разделителями не пропустит валидация");
    }
}
