
package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Maps database records into Book objects
    private Book mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {

        Date date = rs.getDate("published_date");

        return new Book(
                rs.getInt("book_id"),
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("category"),
                rs.getString("publisher"),
                rs.getBigDecimal("price"),
                date == null ? null : date.toLocalDate()
        );
    }

    // CREATE or UPDATE
    public void save(Book book) {

        if (book.getBookId() == 0) {

            String sql = """
                INSERT INTO books
                (isbn, title, author, category, publisher, price, published_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

            jdbcTemplate.update(sql,
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPublisher(),
                    book.getPrice(),
                    book.getPublishedDate()
            );

        } else {

            String sql = """
                UPDATE books
                SET isbn = ?, title = ?, author = ?,
                    category = ?, publisher = ?, price = ?,
                    published_date = ?
                WHERE book_id = ?
                """;

            jdbcTemplate.update(sql,
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPublisher(),
                    book.getPrice(),
                    book.getPublishedDate(),
                    book.getBookId()
            );
        }
    }

    // READ ALL
    public List<Book> findAll() {

        String sql = "SELECT * FROM books ORDER BY book_id DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    // READ ONE
    public Optional<Book> findById(int id) {

        String sql = "SELECT * FROM books WHERE book_id = ?";

        List<Book> books = jdbcTemplate.query(
                sql,
                (rs, rowNum) -> mapRow(rs),
                id
        );

        return books.stream().findFirst();
    }

    // DELETE
    public void delete(int id) {

        String sql = "DELETE FROM books WHERE book_id = ?";

        jdbcTemplate.update(sql, id);
    }
}
