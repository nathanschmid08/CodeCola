<h1 align="center">
  <img src="md/font.png" alt="Code Cola"> 
</h1>

CodeCola is a creative Java project that transforms basic Java code snippets into step-by-step drink recipes. It's especially designed for beginners or anyone who enjoys a more visual and playful interpretation of programming logic.

## ✨ Features

- 🔄 Code-to-recipe transformation – turns Java logic into recipe steps.
- 🎨 Minimalistic GUI with a modern flat design.
- 📦 Supports variables, methods, loops, and conditions.
- 🧠 Smart unit detection – automatically uses ml, g, °C, etc.
- ☕ Built-in examples like Cola, Cocktail, Coffee, and more.

## 📸 Example

```java
int water = 200;
int sugar = 50;
String flavor = "lime";
boolean carbonated = true;

boil(water);
add(sugar);
addFlavor(flavor);
if (carbonated) {
    mix();
}
serve();
```

Is converted into:
```yaml
Prepare 200 ml water  
Prepare 50 g sugar  
Select "lime" as flavor  
Set carbonated to on  
Heat 200 ml  
Add 50 g  
Add lime  
If carbonated, then:  
Mix everything  
Serve everything
```

## 🚀 Getting Started

### Requirements
- Java 17 or newer
- Any IDE (IntelliJ, Eclipse) or terminal with javac and java

### Run
```bash
javac CodeColaGUI.java
java CodeColaGUI
```

## 📂 Built-in Examples

Choose from several sample "recipes":

- Simple Example – A basic drink with sugar and flavor
- Advanced Cola – With temperature, caramel, and stirring
- Cocktail Recipe – Includes lime, rum, and garnish
- Coffee Preparation – Classic coffee with optional plant milk

## 🧭 Supported Java Syntax

Primitive types: `int`, `double`, `String`, `boolean`
Method calls: `boil(...)`, `add(...)`, `serve()`, etc.
Control structures: `if`, `for`, `while`
Class and method definitions (used for recipe steps)

## ❓ Help

Press F1 or click the ? icon in the app for help with syntax, examples, and tips.

