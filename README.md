# 🏦 Java Banking System — OOP

**Author:** Malek Bouaziz
**Course:** Object-Oriented Software Development (OOS)
**Language:** Java 17 · Maven · JavaFX 22

A private bank management system built with full OOP design. Supports account
management, payments & transfers, JSON persistence, and a JavaFX GUI.

---

## ⚙️ Features

- Create / delete accounts and list all transactions
- Transaction types: Payment (deposit/withdrawal), IncomingTransfer, OutgoingTransfer
- Interest & fee calculation on all transactions
- JSON persistence via Gson with a custom TypeAdapter
- 5 custom domain exceptions
- JavaFX two-screen GUI (account list + transaction detail)
- JUnit 5 test suite

---

## 🧱 Class Hierarchy
Transaction  (abstract)
├── Payment
└── Transfer  (abstract)
├── IncomingTransfer
└── OutgoingTransfer
Bank  (interface)
└── PrivateBank
---

## 🚀 Build & Run
```bash
mvn clean install   # build
mvn javafx:run      # launch GUI
mvn test            # run tests
