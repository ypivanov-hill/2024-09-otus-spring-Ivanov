import {BookModel} from './BookModel';

export interface CommentModel {
  id: string;
  text:string;
  book: BookModel
}
