import { Directive } from '@angular/core';
import { HostListener, HostBinding, EventEmitter, Output } from '@angular/core';

@Directive({
  selector: '[appDraggable]'
})
export class DraggableDirective {
  @Output() private filesChangeEmiter: EventEmitter<FileList> = new EventEmitter();

  constructor() {}

  @HostBinding('style.background') private background = '#eee';

  @HostListener('dragover', ['$event'])
  onDragOver(evt) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#999';
  }

  @HostListener('dragleave', ['$event'])
  public onDragLeave(evt) {
    evt.preventDefault();
    evt.stopPropagation();
    this.background = '#eee';
    // do some stuff
  }

  @HostListener('drop', ['$event'])
  public onDrop(evt) {
    evt.preventDefault();
    evt.stopPropagation();
    console.log('files have been dropped');
    this.background = '#eee';
    const files = evt.dataTransfer.files;
    if (files.length > 0) {
      this.filesChangeEmiter.emit(files);
    }
  }
}
