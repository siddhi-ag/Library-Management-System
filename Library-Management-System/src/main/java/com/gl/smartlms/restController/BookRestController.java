package com.gl.smartlms.restController;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gl.smartlms.model.Book;
import com.gl.smartlms.model.Category;
import com.gl.smartlms.repository.BookRepository;
import com.gl.smartlms.service.BookService;
import com.gl.smartlms.service.CategoryService;

@RestController
@RequestMapping("/book")
public class BookRestController {
	
	@Autowired
	private BookService bookService;
	
	@Autowired
	private CategoryService categoryService;
	
	@PostMapping("/badd/{id}")
	public ResponseEntity<String>  addBook(@RequestBody Book book,@PathVariable ("id") Long id ){
		Optional<Category> optional = categoryService.getCategory(id);
		if(optional.isPresent()) {
			book.setCategory(optional.get());
			
			if( bookService.getByTag(book.getTag()) != null ) {
				
				return new ResponseEntity<String>("tag already exist" ,HttpStatus.NOT_ACCEPTABLE);
			} else {
				bookService.addNewBook(book);
				
				return new ResponseEntity<String>("Book get Added with Title " + book.getTitle() +" and Category" + optional.get().getName() ,HttpStatus.CREATED);
			}
			
		}
		
		 return new ResponseEntity<String>("Category Not Available" ,HttpStatus.OK);
		
	}
	
	//update book with same category
	@PutMapping("/bupdate")
	public ResponseEntity<String> updateBook(@RequestBody Book book){
		Optional<Book> optional = bookService.getBookById(book.getId());
		if(optional.isPresent()) {
			Optional<Category> category = categoryService.getCategory(optional.get().getCategory().getId());
			
			book.setCategory(category.get());
			book.setCreateDate(optional.get().getCreateDate());
			book.setStatus(optional.get().getStatus());
			bookService.saveBook(book);
			return new ResponseEntity<String>("Succesfully Updated Book Details", HttpStatus.ACCEPTED);
		}
		return new ResponseEntity<String>("Details Not updated" , HttpStatus.NOT_ACCEPTABLE);
		
	}
	
	//update book as well as  Change Category
	@PutMapping("/bupdate/{id}")
	public ResponseEntity<String> updateBook(@RequestBody Book book,@PathVariable Long id){
		ObjectMapper Obj = new ObjectMapper();
		Optional<Book> optional = bookService.getBookById(book.getId());
		if(optional.isPresent()) {
			Optional<Category> category = categoryService.getCategory(id);
			book.setCategory(category.get());
			book.setCreateDate(optional.get().getCreateDate());
			book.setStatus(optional.get().getStatus());
			bookService.saveBook(book);
			try {
				String bookJson = Obj.writeValueAsString(book);
				return new ResponseEntity<String>("Succesfully Updated Book Details"+bookJson, HttpStatus.ACCEPTED);
			} catch (JsonProcessingException e) {
				
				e.printStackTrace();
			}
			
		}
		return new ResponseEntity<String>("Details Not updated" , HttpStatus.NOT_ACCEPTABLE);
		
	}
	
	



}