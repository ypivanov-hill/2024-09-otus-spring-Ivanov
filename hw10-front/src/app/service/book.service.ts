import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { BookModel } from '../model/BookModel';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class BookService {

  constructor(private http: HttpClient) { }

  public findAllBooks() {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book";
    let booksRequest  = this.http.get<BookModel[]>(url, httpOptions);
    return booksRequest;
  }

  public save(book: BookModel){
    let returnedBook;
    if(book.id === null || book.id === ""){
      returnedBook = this.insert(book);
    } else {
      returnedBook = this.update(book);
    }
    return returnedBook;
  }

  public update(book: BookModel) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book";
    let booksRequest  = this.http.put<BookModel>(url, book, httpOptions);
    return booksRequest;
  }

  public insert(book: BookModel) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book";
    let booksRequest  = this.http.post<BookModel>(url, book, httpOptions);
    return booksRequest;
  }

  public findById(bookId: string) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book/" + bookId;
    let booksRequest  = this.http.get<BookModel>(url, httpOptions);
    return booksRequest;
  }

  public deleteBookById(bookId:string) {
    let httpHeaders = new HttpHeaders()
      .set("Content-Type", "application/json");

    let httpParams = new HttpParams();

    let httpOptions = {
      headers: httpHeaders,
      params: httpParams
    };
    let url: string = "/api/v1/book/"+bookId;
    let booksRequest  = this.http.delete<BookModel[]>(url, httpOptions);
    return booksRequest;
  }
}
