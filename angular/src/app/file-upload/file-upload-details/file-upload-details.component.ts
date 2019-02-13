import {Component, Input, OnInit} from '@angular/core';
import {FileUploadInfo, UploaderService} from '../../uploader.service';
import {NgbProgressbarConfig} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-file-upload-details',
  templateUrl: './file-upload-details.component.html',
  styleUrls: ['./file-upload-details.component.css'],
  providers: [NgbProgressbarConfig]
})
export class FileUploadDetailsComponent implements OnInit {

  @Input()
  fileUploadInfo: FileUploadInfo;

  constructor(config: NgbProgressbarConfig, private $uploader: UploaderService) {
    config.striped = true;
    config.animated = true;
    config.height = '20px';
  }

  downloadAcks(fileId: string, filename: string) {
    this.$uploader.getAcks(fileId).subscribe(
      data => {
        const acks = data.join('\r');
        const element = document.createElement('a');
        element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(acks));
        element.setAttribute('download', filename + '-acks.txt');
        element.style.display = 'none';
        document.body.appendChild(element);
        element.click();
        document.body.removeChild(element);
      }
    );
  }



  ngOnInit() {
  }

}
