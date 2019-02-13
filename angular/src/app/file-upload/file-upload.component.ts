import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.css']
})
export class FileUploadComponent implements OnInit {

  uploadInfo: FileUploadInfo = {
    fileId: ''
  };

  constructor() { }

  update() {
    this.uploadInfo = {
      fileId : this.uploadInfo.fileId + '*'
    };
  }

  ngOnInit() {
  }

}

export interface FileUploadInfo {
    fileId: string;
}
