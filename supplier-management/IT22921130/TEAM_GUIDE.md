# Team Guide

How to add your component. The project uses a **layer-based architecture**: each layer is a package, and every member adds their classes into the shared layer packages. Inventory (Component 3) is done, so use it as the example.

## 1. Get the project

1. Accept the GitHub collaborator invite.
2. Clone: `git clone https://github.com/IT19189390/online-bookstore.git`
3. Switch to your branch: `git checkout book-module`
4. Set up and run it as described in `README.md`.

## 2. Your names

| Component | Branch | Prefix | Table | URL |
|-----------|--------|--------|-----------|-----|
| 1 Book Management | `book-module` | `Book` | `books` | `/books` |
| 2 Customer Management | `customer-module` | `Customer` | `customers` | `/customers` |
| 4 Order / Sales Management | `order-module` | `Order` | `orders` | `/orders` |
| 5 Supplier Management | `supplier-module` | `Supplier` | `suppliers` | `/suppliers` |
| 6 Staff / User Management | `staff-module` | `Staff` | `staff` | `/staff` |

## 3. Where your files go

Add **your own files** into each layer (example for Book Management):

```
com.bookstore
├── controller   BookController.java
├── service      BookService.java              (interface)
│   └── impl     BookServiceImpl.java
├── repository   BookRepository.java          (SQL with JdbcTemplate)
├── model        Book.java (abstract), PrintedBook.java, EBook.java
├── dto          BookForm.java                 (if you use a form object)
└── exception    BookNotFoundException.java

src/main/resources/templates/books/   list.html, form.html, view.html
schema.sql / data.sql   add your CREATE TABLE and INSERTs
```

**Rule:** only the controller calls the service, only the service calls the repository, and only the repository reads/writes files.

### Model (`model` package)
```java
package com.bookstore.model;

public abstract class Book {
    private String bookId;      // private = encapsulation
    private String title;
    // getters/setters with validation

    public abstract String getDisplayInfo();   // overridden by subclasses = polymorphism
}
```

### Database (`schema.sql` and `data.sql`)
Add your table to the end of `src/main/resources/schema.sql`:
```sql
CREATE TABLE IF NOT EXISTS books (
  book_id   VARCHAR(10)  NOT NULL,
  book_type VARCHAR(10)  NOT NULL,
  title     VARCHAR(150) NOT NULL,
  PRIMARY KEY (book_id)
);
```
Add 5 to 10 sample rows to `data.sql` with `INSERT IGNORE INTO books ...`.

### Repository (`repository` package): use `JdbcTemplate`
```java
package com.bookstore.repository;

@Repository
public class BookRepository {
    private final JdbcTemplate jdbc;

    public BookRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Book> findAll() {
        return jdbc.query("SELECT * FROM books ORDER BY book_id", this::mapRow);
    }

    public void insert(Book b) {
        jdbc.update("INSERT INTO books (book_id, book_type, title) VALUES (?, ?, ?)",
                b.getBookId(), b.getBookType(), b.getTitle());
    }

    public boolean deleteById(String id) {
        return jdbc.update("DELETE FROM books WHERE book_id = ?", id) > 0;
    }

    private Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        // read columns and create the right subclass based on book_type
    }
}
```
Always use `?` placeholders, never string concatenation, to prevent SQL injection.
See `InventoryRepository` for a complete example with insert, update and subclass mapping.

### Service (`service` + `service/impl`)
```java
package com.bookstore.service;
public interface BookService {
    List<Book> getAll();
    Book create(BookForm form);
    // ...
}

package com.bookstore.service.impl;
@Service
public class BookServiceImpl implements BookService { ... }
```

### Controller (`controller` package)
```java
package com.bookstore.controller;

@Controller
@RequestMapping("/books")
public class BookController {
    private final BookService service;          // depend on the interface

    @ModelAttribute("active")
    public String activeModule() { return "books"; }   // highlights the sidebar

    @GetMapping
    public String list(Model model) { ...; return "books/list"; }
}
```
Always name path variables: `@PathVariable("id") String id`.

### Templates
Copy the shape of `templates/inventory/*.html` and reuse:
`~{fragments/layout :: head('Books')}`, `:: sidebar`, `:: flash`.

### Turn on your dashboard link
In `model/DashboardModule.java`, change only your line:
```java
new DashboardModule("books", "Book Management",
        "Add, search and edit books in the catalogue.", "/books", true),
```

## 4. Minimum requirements
- ✅ At least 3 CRUD operations, saved to your own MySQL table
- ✅ At least 3 UI pages
- ✅ Encapsulation, inheritance and polymorphism in your model classes
- ✅ Be ready to explain your backend Java in the viva

## 5. Commit and merge
1. Commit small and often: `Add Book model classes`, `Add BookRepository file handling`...
2. Push your branch.
3. On GitHub: **Compare & pull request** into `master`, get one approval, merge.
4. Pull `master` before starting each day.

## Rules
- ❌ Don't push directly to `master`.
- ❌ Don't edit another member's classes.
- ⚠️ Shared files (`DashboardModule`, `schema.sql`, `data.sql`, `layout.html`, `app.css`): change only what you need and tell the team.
- ✅ Commit from your own GitHub account.
