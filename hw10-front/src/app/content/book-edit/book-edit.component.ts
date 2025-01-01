import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';
import { BookService } from '../../service/book.service';
import { BookModel } from '../../model/BookModel';
import { Select } from 'primeng/select'
import { AuthorModel } from '../../model/AuthorModel';
import { AuthorService } from '../../service/author.service';
import { firstValueFrom } from 'rxjs';
import { GenreService } from '../../service/genre.service';
import { GenreModel } from '../../model/GenreModel';
import { Book } from '../../model/Book';

@Component({
  selector: 'app-book-edit',
  templateUrl: './book-edit.component.html',
  styleUrl: './book-edit.component.scss',
  providers: [MessageService, Select]
})
export class BookEditComponent  implements OnInit {

  public bookId!:string;
  public book!:BookModel;
  public authorsOptions!:AuthorModel[];
  public genreOptions!:GenreModel[];

  public constructor(private route: ActivatedRoute,
                     private router: Router,
                     private message:MessageService,
                     private bookService: BookService,
                     private authorService:AuthorService,
                     private genreService: GenreService) { }

  ngOnInit() {
    let pathBookId = this.route.snapshot.paramMap.get("bookId");
    this.bookId = pathBookId === null ?"":pathBookId;

    if("new" === this.bookId) {
      this.book = new Book();

    }else {
      this.bookService.findById(this.bookId).subscribe({
        next: (v) => {
          this.book = v;
        },
        error: (err) => {
          debugger
          this.message.add({severity: 'error', summary: 'error', detail: err.error, life: 3000});
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'complete', life: 3000});
        }
      });
    }

    firstValueFrom(this.authorService.findAllAuthor())
      .then((value) => {
        console.log(`findAllAuthor: ` + value.length);
        this.authorsOptions = value;
      });
    firstValueFrom(this.genreService.findAllGenre())
      .then((value) => {
        console.log(`findAllGenre: ` + value.length);
        this.genreOptions = value;
      });
  }

  public save(){
    this.bookService.save(this.book).subscribe({
      next: (v) => {
        this.book = v;
      },
      error: (err) => {
        this.message.add({ severity: 'error', summary: 'error', detail: err.error, life: 3000 });
        console.log("error2 " + err.toString());
      },
      complete: () => {
        this.message.add({ severity: 'info', summary: 'Info', detail: 'Save Done' , life: 3000 });
        if(this.bookId === "new") {
          this.router.navigate(['/book-edit/',  this.book.id ]);
        }
      }
    });
  }

}
