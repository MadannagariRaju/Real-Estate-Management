import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-message-customer',
  templateUrl: './message-customer.component.html',
  styleUrls: ['./message-customer.component.css']
})
export class MessageCustomerComponent implements OnInit {
  messages: any[] = []; 
  errorMessage: string | null = null; 

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchMessages();
  }

  fetchMessages(): void {
    const url = 'http://localhost:8080/messages/getmessage'; 
    this.http.get(url, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
      }),
      withCredentials: true, // Include credentials (cookies, session tokens, etc.)
    }).subscribe({
      next: (response: any) => {
        console.log('Response:', response);  
        this.messages = response['Messages'];
        console.log("Messages:", this.messages);

        // Check if messages exist in the response
        if (response && response.Messages && response.Messages.length > 0) {
          this.messages = response.Messages;  
          this.messages.forEach(message => {
            message.userProfile = {
              name: message.Name,
              email: message.Email,
              phone: message['Phone'],
              address: message.Address
            };
          });
        } else {
          this.errorMessage = 'No messages found.';
        }

      },
      error: (error: any) => {
        console.error('Error fetching messages:', error);
        this.errorMessage = 'Failed to load messages. Please try again later.';
      }
    });
  }

  deleteMessage(messageId: number): void {
    const url = `http://localhost:8080/messages/delete/${messageId}`;

    console.log(messageId)
    console.log(typeof messageId)
    
    this.http.delete(url, { withCredentials: true }).subscribe({
      next: () => {
        this.messages = this.messages.filter(message => message.messageId !== messageId);
        console.log(`Message with ID ${messageId} deleted successfully.`);
      },
      error: (error: any) => {
        console.error(`Error deleting message with ID ${messageId}:`, error);
        this.errorMessage = `Failed to delete message. Please try again later.`;
      }
    });
  }
  
}
