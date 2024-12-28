import { Component, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { AuthorModel } from '../../model/AuthorModel';
import { AuthorService } from '../../service/author.service';

@Component({
  selector: 'app-author-list',
  templateUrl: './author-list.component.html',
  styleUrl: './author-list.component.scss',
  providers:[MessageService]
})
export class AuthorListComponent implements OnInit {

  public author!: AuthorModel[];

  constructor(private authorService:AuthorService,
              private message:MessageService) {
  }

  ngOnInit(): void {
    this.authorService.findAllAuthor()

      .subscribe({
        next: (v) => {
          this.author = v;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Load complete', life: 3000});
        }
      });
  }

  delete(author:AuthorModel) {
    this.authorService.deleteAuthorById(author.id)
      .subscribe({
        next: (v) => {
          this.author = v;
        },
        error: (err) => {
         this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Delete complete', life: 3000});
        }
      });
  }

}
