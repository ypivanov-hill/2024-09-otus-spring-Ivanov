import { CommentModel } from './CommentModel';
import { BookModel } from './BookModel';

export class Comment implements CommentModel {

  id: string;
  text: string;
  book: BookModel;

  constructor(text: string, book: BookModel) {
    this.id = "";
    this.text = text;
    this.book = book;
  }


}
