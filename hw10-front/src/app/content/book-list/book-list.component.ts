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
              private router: Router,
              private message:MessageService ) {

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
    this.isLoadingResult = true;
    this.bookService.deleteBookById(book.id).subscribe({
      next: (v) => {
        let index = this.books.findIndex(a => a.id === v); 
        this.books.splice(index, 1);
        this.isLoadingResult = false;
        this.message.add({severity: 'info', summary: 'Info', detail: 'Delete complete', life: 3000});
      },
      error: (err) => {
        this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
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
