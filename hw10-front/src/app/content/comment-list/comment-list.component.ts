import { Component, OnInit } from '@angular/core';
import { CommentService } from '../../service/comment.service';
import { ActivatedRoute } from '@angular/router';
import { MessageService } from 'primeng/api';
import { CommentModel } from '../../model/CommentModel';
import { Comment } from '../../model/Comment';
import { Book } from '../../model/Book';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrl: './comment-list.component.scss',
  providers: [MessageService]
})
export class CommentListComponent implements OnInit {

  public bookId!: string;
  public selectedComment: CommentModel;

  public comments!: CommentModel[];
  public isLoadingResult: boolean = false;
  public visibleEdit: boolean = false;
  public visibleNew: boolean = false;

    constructor(private commentService:CommentService,
                private route: ActivatedRoute,
                private message:MessageService) {
    this.selectedComment = new Comment("", new Book());
    }

  ngOnInit(): void {
    let pathBookId = this.route.snapshot.paramMap.get("bookId");
    this.bookId = pathBookId === null ?"":pathBookId;
    this.commentService.findAllByBookId(this.bookId)

      .subscribe({
        next: (v) => {
          this.comments = v;
          this.isLoadingResult = false;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
          this.isLoadingResult = false;
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Load complete', life: 3000});
          this.isLoadingResult = false;
        }
      });
  }

  showDialog(comment: CommentModel) {
    console.log("showDialog comment id {} ", comment.text)
      this.selectedComment = new Comment(comment.text, comment.book);
      this.selectedComment.id = comment.id;
      this.visibleEdit = true;
  }

  saveDialog() {
      console.log("saveDialog Comment id {} ", this.selectedComment.text)
      this.commentService.update(this.bookId, this.selectedComment)
      .subscribe({
        next: (v) => {
          let index = this.comments.findIndex(a => a.id === v.id); 
          this.comments[index] = v;
          this.isLoadingResult = false;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
          this.isLoadingResult = false;
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Save complete', life: 3000});
          this.isLoadingResult = false;
        }
      });

    this.visibleEdit = false;
  }

  deleteComment(comment: CommentModel) {
   this.commentService.deleteCommentById(this.bookId, comment.id)

      .subscribe({
        next: (v) => {
          let index = this.comments.findIndex(a => a.id === v); 
          this.comments.splice(index, 1);
          this.isLoadingResult = false;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
          this.isLoadingResult = false;
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Delete complete', life: 3000});
          this.isLoadingResult = false;
        }
      });
  }

  cancelDialog() {
    this.visibleEdit = false;
  }

  showNewCommentDialog() {
    let currentBook = new Book();
    currentBook.id = this.bookId;
    this.selectedComment = new Comment("", currentBook);
    this.visibleNew = true;
  }

  saveNewCommentDialog() {

    this.commentService.insert(this.bookId, this.selectedComment)

      .subscribe({
        next: (v) => {
          this.comments.push(v);
          this.isLoadingResult = false;
        },
        error: (err) => {

          this.message.add({severity: 'error', summary: 'Error', detail: err.error, life: 3000});
          this.isLoadingResult = false;
        },
        complete: () => {
          this.message.add({severity: 'info', summary: 'Info', detail: 'Insert complete', life: 3000});
          this.isLoadingResult = false;
        }
      });
    this.visibleNew = false;
  }

  cancelNewCommentDialog() {
    this.visibleNew = false;
  }
}
