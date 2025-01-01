import {AuthorModel} from './AuthorModel';
import {GenreModel} from './GenreModel';

export interface BookModel {
  id: string;
  title: string;
  author: AuthorModel;
  genres: GenreModel[];
}
