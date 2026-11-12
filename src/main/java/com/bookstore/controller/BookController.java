
package com.bookstore.controller;

import com.bookstore.dto.BookForm;
import com.bookstore.model.Book;
import com.bookstore.service.BookService;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/modules/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Highlight Book Management in the shared sidebar
    @ModelAttribute("active")
    public String activeModule() {
        return "books";
    }

    // Display all books
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books/list";
    }

    // Display add book form
    @GetMapping("/new")
    public String showAddForm(Model model) {
        model.addAttribute("bookForm", new BookForm());
        return "books/form";
    }

    // Display edit book form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") int id, Model model) {

        Book book = bookService.getBookById(id).orElse(null);

        if (book == null) {
            return "redirect:/modules/books";
        }

        BookForm form = new BookForm();

        form.setBookId(book.getBookId());
        form.setIsbn(book.getIsbn());
        form.setTitle(book.getTitle());
        form.setAuthor(book.getAuthor());
        form.setCategory(book.getCategory());
        form.setPublisher(book.getPublisher());
        form.setPrice(book.getPrice());
        form.setPublishedDate(book.getPublishedDate());

        model.addAttribute("bookForm", form);

        return "books/form";
    }

    // Save new book or update existing book
    @PostMapping("/save")
    public String saveBook(
            @Valid @ModelAttribute("bookForm") BookForm form,
            BindingResult result) {

        if (result.hasErrors()) {
            return "books/form";
        }

        Book book = new Book();

        book.setBookId(form.getBookId());
        book.setIsbn(form.getIsbn());
        book.setTitle(form.getTitle());
        book.setAuthor(form.getAuthor());
        book.setCategory(form.getCategory());
        book.setPublisher(form.getPublisher());
        book.setPrice(form.getPrice());
        book.setPublishedDate(form.getPublishedDate());

        bookService.saveBook(book);

        return "redirect:/modules/books";
    }

    // Display book details
    @GetMapping("/details/{id}")
    public String showBookDetails(
            @PathVariable("id") int id,
            Model model) {

        Book book = bookService.getBookById(id).orElse(null);

        if (book == null) {
            return "redirect:/modules/books";
        }

        model.addAttribute("book", book);

        return "books/details";
    }

    // Delete book
    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable("id") int id) {

        bookService.deleteBook(id);

        return "redirect:/modules/books";
    }
}
