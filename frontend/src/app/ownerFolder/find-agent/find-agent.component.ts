import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-find-agent',
  templateUrl: './find-agent.component.html',
  styleUrls: ['./find-agent.component.css']
})
export class FindAgentComponent {
  businessProfiles: any[] = [];
  filteredProfiles: any[] = [];
  selectedProfile: any | null = null;
  reviews: any[] = [];
  agentId?: number; 
  filters = {
    location: '',
    experience: null,
    specialization: ''
  };

  contactProfile: any = null;
  contactMessage: string = '';

  contactedAgents : any[] = [];

  contactedAgentsSet = new Set<number>();

  contactedAgentsIds : any[] =[];
  showContactedAgents = false;

  reviewProfile: any = null; 
  reviewMessage: string = '';

  constructor(private http: HttpClient, private toastr: ToastrService) {}

  ngOnInit(): void {
    this.fetchAgentDetails();
    this.getMessages();
  }

  toggleView(): void {
    this.showContactedAgents = !this.showContactedAgents;
  
    if (this.showContactedAgents) {
      this.getMessages();
    }
  }
  
  fetchAgentDetails(): void {
    const url = 'http://localhost:8080/business/get-all-business-profiles';
    this.http.get(url).subscribe({
      next: (response: any) => {
        console.log("agent Details ",response)
        this.businessProfiles = response['allBusinessProfiles'];
        this.filteredProfiles = [...this.businessProfiles]; 
      },
      error: (error: any) => {
        console.error('Error fetching business profiles:', error);
      }
    });
  }

  applyFilters(): void {
    console.log("filters :",this.filters)
    this.filteredProfiles = this.businessProfiles.filter(profile => {
      const matchesLocation = !this.filters.location || profile.location?.toLowerCase().includes(this.filters.location.toLowerCase());
      console.log("this.filters.location",this.filters.location);
      console.log("profile.location",profile.location)
      const matchesExperience = this.filters.experience === null || profile.experience >= (this.filters.experience || 0);
      console.log("this.filters.experience",this.filters.experience);
      console.log("profile.experience",profile.experience)
      const matchesSpecialization = !this.filters.specialization || profile.specialization?.toLowerCase().split(/\s*,\s*|\s+/).includes(this.filters.specialization.toLowerCase());
      console.log("this.filters.specialization",this.filters.specialization);
      console.log("profile.specialization",profile.specialization)
      return matchesLocation && matchesExperience && matchesSpecialization;
    });
    console.log(this.filteredProfiles)
  }

  resetFilters(): void {
    this.filters = { location: '', experience: null, specialization: '' };
    this.filteredProfiles = [...this.businessProfiles];
  }

  viewDetails(profile: any): void {
    this.selectedProfile = profile;
    this.getReviews(profile.id); 
  }

  closeDetails(): void {
    this.selectedProfile = null;
    this.reviews = [];
  }

  openContactBox(profile: any): void {
    this.contactProfile = profile;
    this.contactMessage = '';
  
  }

  closeContactBox(): void {
    this.contactProfile = null;
  }

  sendMessage(profile: any): void {
    if (this.contactMessage.trim()) {
      const messageData = {
        agentId: profile.user.id,
        message: this.contactMessage,
      };

      this.http.post('http://localhost:8080/business/post-messages', messageData, { withCredentials: true }).subscribe(
        (response: any) => {
          console.log('Message sent successfully:', response);
          this.closeContactBox();
          this.toastr.success('Message send successfully !', 'Success', {
            timeOut: 3000,
            progressBar: true
          });

        if (!this.contactedAgentsIds.includes(profile.user.id)) {
          this.contactedAgents.push(profile); // Add the agent to the list
          this.contactedAgentsIds.push(profile.user.id); // Update the IDs array
        }

        },
        (error: any) => {
          this.toastr.error('error occured on sending message !', 'error', {
            timeOut: 5000,
            progressBar: true
          });
          console.error('Error sending message:', error);
        }
      );
    } else {
      alert('Please enter a message before sending.');
    }
  }

  openReviewBox(profile: any): void {
    this.reviewProfile = profile;
    this.reviewMessage = '';
  }

  closeReviewBox(): void {
    this.reviewProfile = null;
  }

  submitReview(profile: any): void {
    if (this.reviewMessage.trim()) {
      const reviewData = {
        reviewDescription: this.reviewMessage,
        agentId: profile.id
      };

      this.http.post('http://localhost:8080/review/agent?agentId=' + profile.id, reviewData, { withCredentials: true }).subscribe(
        (response: any) => {
          console.log('Review submitted successfully:', response);
          this.closeReviewBox();
          this.toastr.success('Review send successfully !', 'Success', {
            timeOut: 5000,
            progressBar: true
          });
        },
        (error: any) => {
          console.error('Error submitting review:', error);
          this.toastr.error('Error occured on Review send successfully !', 'error', {
            timeOut: 5000,
            progressBar: true
          });
        }
      );
    } else {
      alert('Please enter a review before submitting.');
    }
  }

  getReviews(agentId: number): void {
    this.agentId = agentId;
    this.http.get(`http://localhost:8080/review/agent?agentId=${this.agentId}`, { withCredentials: true }).subscribe(
      (data: any) => {
        this.reviews = data;
      },
      (error: any) => {
        console.error('Error fetching reviews:', error);
      }
    );
  }

  getMessages(): void {
    this.http.get('http://localhost:8080/review/get-message', { withCredentials: true }).subscribe(
      (data: any) => {
        console.log('Response Data:', data);
  
        if (Array.isArray(data.contactedAgents)) {
          const uniqueAgentsMap = new Map<number, any>();
  
          data.contactedAgents.forEach((agent: any) => {
            uniqueAgentsMap.set(agent.agentId, agent);
          });
  
          this.contactedAgents = Array.from(uniqueAgentsMap.values());
          console.log("Updated unique contactedAgents", this.contactedAgents);

                  // Create a separate array for agent IDs
          this.contactedAgentsIds = this.contactedAgents.map(agent => agent.agentId);
          console.log("Contacted Agents IDs:", this.contactedAgentsIds);
  
        } else {
          console.error('Expected an array but received:', data);
        }
      },
      (error: any) => {
        console.error('Error fetching messages:', error);
      }
    );
  }

  
}
