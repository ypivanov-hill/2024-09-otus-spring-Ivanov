import { Component, OnInit } from '@angular/core';
import { GenreModel } from '../../model/GenreModel';
import { GenreService } from '../../service/genre.service';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-genre-list',
  templateUrl: './genre-list.component.html',
  styleUrl: './genre-list.component.scss',
  providers:[MessageService]
})
export class GenreListComponent implements OnInit {

  public genre!: GenreModel[];

  constructor(private genreService:GenreService,
              private message:MessageService) {
  }

  ngOnInit(): void {
    this.genreService.findAllGenre()
      .subscribe({
        next: (v) => {
          this.genre = v;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Load complete', life: 3000});
        }
      });
  }

}
