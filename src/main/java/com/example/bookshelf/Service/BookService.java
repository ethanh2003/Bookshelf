package com.example.bookshelf.Service;

import com.example.bookshelf.model.Book;
import com.example.bookshelf.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
    private static BookRepository bookRepository;


    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {
        bookRepository.save(book);
    }

    public static List<Book> getUserBooks(Long userId) {
        return bookRepository.findByUserId(userId);
    }
}
