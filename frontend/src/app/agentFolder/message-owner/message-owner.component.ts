import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-message-owner',
  templateUrl: './message-owner.component.html',
  styleUrls: ['./message-owner.component.css']
})
export class MessageOwnerComponent implements OnInit {
  messages: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchMessages();
  }

  fetchMessages(): void {
    console.log("Fetching messages...");
    const url = 'http://localhost:8080/business/get-messages';

    this.http.get(url, { withCredentials: true }).subscribe({
      next: (response: any) => {
        console.log('Response received:', response);

        // Ensure response is an array or extract the relevant data
        this.messages = Array.isArray(response) ? response : response.message || [];
        console.log('Messages length:', this.messages.length);
      },
      error: (error: any) => {
        console.error('Error fetching messages:', error);
      }
    });
  } 
  

  deleteMessage(messageId: number, index: number): void {
    const url = `http://localhost:8080/business/delete-message/${messageId}`;
      this.http.delete(url, { withCredentials: true }).subscribe({
        next: () => {
          console.log(`Message with ID ${messageId} deleted successfully.`);
          // Remove the message from the array
          this.messages.splice(index, 1);
        },
        error: (error: any) => {
          console.error('Error deleting message:', error);
        }
      });
    }
}
