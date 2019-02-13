import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {interval, Observable} from 'rxjs';
import {flatMap, pluck, takeWhile} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UploaderService {

  constructor(private $http: HttpClient) {
  }

  uploadFileToUrl(file: File): Observable<FileUploadInfo> {
    const form: FormData = new FormData();
    form.append('file', file);
    return this.$http.post<FileUploadInfo>('file/upload-messages', form);
  }

  initiateFileProcess(fileId: string): Observable<FileUploadInfo> {
    const form: FormData = new FormData();
    form.append('fileId', fileId);
    return this.$http.post<FileUploadInfo>('file/process-file', form);
  }

  watch(fileId: string): Observable<FileUploadInfo> {
    return interval(1000).pipe(
      flatMap(tick => {
        return this.reportFileProcess(fileId);
      }),
      takeWhile((info: FileUploadInfo) => (info.status === 'started' || info.status === 'reading'))
    );
  }


  reportFileProcess(fileId: string): Observable<FileUploadInfo> {
    return this.$http.get<FileUploadInfo>('file/report-file', {
      params: {
        fileId: fileId
      }
    });
  }

  getQueues(): Observable<FileUploadInfo[]> {
    return this.$http.get('file/get-queues').pipe(
      pluck('uploads')
    );
  }

  pauseFileProcess() {

  }

  unpauseFileProcess() {

  }

  getAcks() {

  }

  removeFile() {

  }
}


export interface FileUploadInfo {
  fileId: string;
  status: string;
  fileName: string;
  numberOfMessages: number;
  numberProcessed: number;
  averageElapsed: number;
  elapsedTimeMs: number;
  numberUnProcessed: number;
  percentage: number;
  deleteRequested: boolean;
  countdown: number;
}
