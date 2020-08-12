import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {FileUploadInfo, ModifierService} from '../../services/modifier.service';
import {NgbProgressbarConfig} from '@ng-bootstrap/ng-bootstrap';
import { interval, concat, of } from 'rxjs';
import { tap, takeWhile } from 'rxjs/operators';

@Component({
  selector: 'app-file-modify-details',
  templateUrl: './file-modify-details.component.html',
  styleUrls: ['./file-modify-details.component.css'],
  providers: [NgbProgressbarConfig]
})
export class FileModifyDetailsComponent implements OnInit {

  @Input()
  fileUploadInfo: FileUploadInfo;
  @Output()
  delete: EventEmitter<string>;
  @Output()
  resume: EventEmitter<string>;
  deleteRequest: boolean;
  countDown: number;

  constructor(config: NgbProgressbarConfig, private $uploader: ModifierService) {
    config.striped = true;
    config.animated = true;
    config.height = '20px';
    this.delete = new EventEmitter<string>();
    this.resume = new EventEmitter<string>();
    this.deleteRequest = false;
  }

  downloadAcks(fileId: string, filename: string) {
    this.$uploader.getAcks(fileId).subscribe(
      data => {
        const acks = data.join('\r');
        const element = document.createElement('a');
        const fileData = new Blob([acks], { type: 'text/plain' });
        element.setAttribute('href', URL.createObjectURL(fileData));
        element.setAttribute('download', filename + '-acks.txt');
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
      }
    );
  }

  requestDelete() {
    this.deleteRequest = true;
    this.countDown = 100;
    this.$uploader.pauseFileProcess(this.fileUploadInfo.fileId).toPromise();

    concat(
      interval(100).pipe(
        tap(() => {
          this.countDown -= 3.8;
        }),
        takeWhile(() => this.countDown > 0),
      ),
      of(true).pipe(
        tap(() => {
          if (this.deleteRequest) {
            this.delete.emit(this.fileUploadInfo.fileId);
          }
        })
      )
    ).toPromise();
  }

  cancelDelete() {
    this.deleteRequest = false;
    this.$uploader.unpauseFileProcess(this.fileUploadInfo.fileId).toPromise();
    this.resume.emit(this.fileUploadInfo.fileId);
  }

  ngOnInit() {
  }

}
