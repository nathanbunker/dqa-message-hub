import {Component, Input, OnInit} from '@angular/core';
import {FileUploadInfo} from '../file-upload.component';

@Component({
  selector: 'app-file-upload-details',
  templateUrl: './file-upload-details.component.html',
  styleUrls: ['./file-upload-details.component.css']
})
export class FileUploadDetailsComponent implements OnInit {

  @Input()
  fileUploadInfo: FileUploadInfo;

  constructor() { }

  ngOnInit() {
  }

}
