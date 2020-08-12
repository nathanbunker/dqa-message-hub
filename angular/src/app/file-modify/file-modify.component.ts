import { Component, OnInit } from '@angular/core';
import { FileUploadInfo, ModifierService } from '../services/modifier.service';
import { concat, Observable, of, interval } from 'rxjs';
import { tap, take } from 'rxjs/operators';

@Component({
  selector: 'app-file-modify',
  templateUrl: './file-modify.component.html',
  styleUrls: ['./file-modify.component.css']
})
export class FileModifyComponent implements OnInit {

  fileUploads: {
    [id: string]: Observable<FileUploadInfo>
  };
  file: File;

  constructor(private $uploader: ModifierService) {
  }

  keysOf(object: any) {
    return Object.keys(object);
  }

  setFile(event) {
    if (event.target.files && event.target.files.length > 0) {
      this.file = event.target.files[0];
    }
  }

  delete(fileId: string) {
    this.$uploader.removeFile(fileId).pipe(
      tap(() => {
        delete this.fileUploads[fileId];
      }),
    ).toPromise();
  }

  resume(fileId: string) {
    this.fileUploads[fileId] = concat(this.fileUploads[fileId], this.$uploader.watch(fileId));
  }

  submit() {
    this.uploadFile(this.file);
  }

  uploadFile(file: File) {
    this.$uploader.uploadFileToUrl(file).subscribe(
      (fileInfo: FileUploadInfo) => {
        this.$uploader.initiateFileProcess(fileInfo.fileId).toPromise();
        this.fileUploads[fileInfo.fileId] = concat(
          of(fileInfo),
          this.$uploader.watch(fileInfo.fileId)
        );
      }
    );
  }

  ngOnInit() {
    this.fileUploads = {};
    this.$uploader.getQueues().subscribe(queue => {
      for (const file of queue) {
        if (file.status !== 'started' && file.status !== 'reading') {
          this.fileUploads[file.fileId] =  of(file);
        } else {
          this.fileUploads[file.fileId] =  concat(of(file), this.$uploader.watch(file.fileId));
        }
      }
    });
  }
}
