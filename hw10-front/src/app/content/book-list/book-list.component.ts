import { Component, OnInit } from '@angular/core';
import { BookModel } from '../../model/BookModel';
import { BookService } from '../../service/book.service';
import { Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss',
  providers:[MessageService]
})
export class BookListComponent  implements OnInit  {

  public isLoadingResult!: boolean;
  public books!: BookModel[];

  constructor(private bookService:BookService,
              private router: Router ) {

  }
  ngOnInit(): void {
    this.isLoadingResult = true;
    this.bookService.findAllBooks()

      .subscribe({
      next: (v) => {
        this.books = v;
        this.isLoadingResult = false;
      },
      error: (err) => {

        console.log("error2 " + err.toString());
        this.isLoadingResult = false;
      },
      complete: () => {
        console.info('complete')
        this.isLoadingResult = false;
      }
    });
  }


  public selectBook(book:BookModel) {
    console.log("book " + book.title);
  }

  public deleteBook(book:BookModel) {
    console.log("deleteBooks book " + book.title);
    this.bookService.deleteBookById(book.id).subscribe({
      next: (v) => {
        this.books = v;
        this.isLoadingResult = false;
      },
      error: (err) => {

        console.log("error2 " + err.toString());
        this.isLoadingResult = false;
      }
    });
  }

  public gotoBook(book: BookModel)
  {
    this.router.navigate(['/book-edit/',  book.id ]);
  }

  public gotoComments(book: BookModel)
  {
    this.router.navigate(['/book-edit/',  book.id , "comment-list"]);
  }

}
