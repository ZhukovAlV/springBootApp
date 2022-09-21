package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.DAOException;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final Storage storage;

    private final BookMapper bookMapper;

    @Autowired
    public BookServiceImpl(Storage storage, BookMapper bookMapper) {
        this.storage = storage;
        this.bookMapper = bookMapper;
    }
    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        Long bookId = book.getId();

        if (bookId != null) {
            Optional<Book> bookFind = storage.getBookById(bookId);

            if (bookFind.isPresent()) {
                throw new DAOException("Книга с ID: " + bookId + " уже имеется в системе и не может быть создана");
            }
        }

        Optional<Book> bookOpt = storage.saveBook(book);

        if (bookOpt.isPresent()) return bookMapper.bookToBookDto(bookOpt.get());
        else throw new DAOException("Книга с ID: " + bookId + " не была сохранена");
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        Optional<Book> bookOpt = storage.updateBook(book);

        if (bookOpt.isPresent()) return bookMapper.bookToBookDto(bookOpt.get());
        else throw new NotFoundException("Книга не найдена");
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> bookOpt = storage.getBookById(id);

        if (bookOpt.isPresent()) return bookMapper.bookToBookDto(bookOpt.get());
        else throw new NotFoundException("Книга с ID: " + id + " не найдена");
    }

    @Override
    public List<Long> getBooksByUserId(Long userId) {
        Optional<User> userOpt = storage.getUserById(userId);

        if (userOpt.isEmpty()) throw new NotFoundException("Пользователь не найден");

        return userOpt.get().getBookList();
    }

    @Override
    public void deleteBookById(Long id) {
        if (!storage.deleteBook(id)) throw new NotFoundException("Книга с ID: " + id + " не была найдена");
    }
}
