
package com.bookstore.service;

import com.bookstore.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> getAllBooks();

    Optional<Book> getBookById(int id);

    void saveBook(Book book);

    void deleteBook(int id);
}
