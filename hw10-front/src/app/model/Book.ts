import { BookModel } from './BookModel';
import { AuthorModel } from './AuthorModel';
import { GenreModel } from './GenreModel';

export class Book implements BookModel {

  public id: string;
  public title: string;
  public author: AuthorModel;
  public genres: GenreModel[];

  public constructor() {
    this.id = "";
    this.title = "";
    this.author = {} as AuthorModel;
    this.genres = [] as GenreModel[];
  }
}
