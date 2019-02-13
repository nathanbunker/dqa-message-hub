import {Component, OnInit} from '@angular/core';
import {FileUploadInfo, UploaderService} from '../uploader.service';
import {concat, Observable, of} from 'rxjs';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  fileUploads: {
    [id: string]: Observable<FileUploadInfo>
  };
  file: File;

  constructor(private $uploader: UploaderService) {
  }

  keysOf(object: any) {
    return Object.keys(object);
  }

  setFile(event) {
    if (event.target.files && event.target.files.length > 0) {
      this.file = event.target.files[0];
    }
  }

  submit() {
    this.uploadFile(this.file);
  }

  uploadFile(file: File) {
    this.$uploader.uploadFileToUrl(file).subscribe(
      (fileInfo: FileUploadInfo) => {
        this.$uploader.initiateFileProcess(fileInfo.fileId).subscribe();
        this.fileUploads[fileInfo.fileId] = concat(of(fileInfo), this.$uploader.watch(fileInfo.fileId));
      }
    );
  }

  ngOnInit() {
    this.fileUploads = {};
    this.$uploader.getQueues().subscribe(queue => {
      for (const file of queue) {
        this.fileUploads[file.fileId] = concat(of(file), this.$uploader.watch(file.fileId));
      }
    });
  }
}
