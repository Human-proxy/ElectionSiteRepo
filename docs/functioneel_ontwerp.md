---
title: Functional Design
children:
---

# Functional Design

## Introduction and Approach

This web application is designed to support Dutch youth in their first experience for Dutch politics. The application specifically targets young citizens who are new to politics and have little or no prior knowledge. It provides clear, accessible information and interactive tools. Through a forum, informative pages about the Dutch political system, and filtered insights into election results and history (where users can ask questions and receive tailored results), the application helps young people to become well-informed and make conscious voting choices. An optional extra feature is a quiz that shows which political party best matches their views. The application is initially developed for the Netherlands, but can be quickly expanded to other EU countries. Requirements are determined in consultation with the Product Owner.

### Functional Requirements

#### Information on the website
1. The system must show results of the most recent elections, including seat distribution per party and vote percentages per region.
2. The system must display election results using:
	- Bar charts for seat distribution per party
	- Pie charts for vote percentages per region
	- Sortable tables for party comparison and filtering by region

#### User interaction (forum)
4. The system must allow users to create new topics (questions) in the Q&A forum.
5. The system must allow users to post answers to topics and upvote helpful answers.
6. The forum must be moderated by admins, who can delete posts and block users for safety and reliability.
7. The system could structure discussions into categories (e.g., Politics, Elections, Parties, Voting Process) and allow users to filter by category.

#### Authentication & Authorization
8. The system must allow users to register with email and password, and receive confirmation of successful registration.
9. The system must allow users to log in securely with email and password, and receive feedback if login fails.
10. The system must restrict access to functionalities based on user roles (user vs. admin), e.g., only admins can moderate forum and manage users.
11. The system must use JWT authentication for secure access to protected pages and actions.

#### Administration (Admin)
12. The system must allow admins to view, add, block, and remove user accounts.
13. The system must allow admins to add, edit, and delete forum posts, categories, and moderate discussions (e.g., resolve reports, block users).

#### Extra Features
14. The system could provide localization to connect users to relevant data.
15. The system could offer a Party Match Helper (quiz) to connect users to a suitable political party.
16. The system could allow account management (change password, update details).
17. The system could support expansion to elections in other EU countries.

### Non-Functional Requirements 

1. The system must be user-friendly and intuitive for young users with little political experience.
2. The website must load within 2 seconds for a smooth user experience.
3. The system must protect user privacy and store personal data minimally and securely.
4. The forum must be monitored and secure to ensure a safe environment for discussions.
5. The website must be accessible according to WCAG 2.1 AA standards.
6. The system must be responsive and work well on desktop, tablet, and mobile devices.
7. The backend must use JWT for secure authentication and authorization.
8. The system must allow for easy import and processing of election results from XML files.
9. The platform should use minimal energy and data to support sustainability.
10. The system should provide clear error messages and feedback for all user actions.

## Key Processes

### Main processes

1. Exploring parliamentary data: Users can interact with a quiz-like filter to select what information they want to see (e.g., how a specific political party performed in a certain region). The system then filters the dataset and displays relevant insights and visualizations.
2. User registration and login
3. Participating in discussions in the forum
4. Managing users and content (admin)

### Optional processes
- Filling in the quiz and viewing which party suits you best
- Managing account
- Providing information about the Dutch political system
- Reading information about the purpose of the website (About page)


## Pages and Features


### Pages and Features (from MoSCoW User Stories)

**Must have:**
- **Home page:** Central dashboard with election results chart, tabs for Insights, Community, and Learn. Main navigation to all features.
- **Login page:** Allows users to securely log in to access their account and platform features.
- **Register page:** Enables new users to create an account with email and password.
- **Navigation bar:** Provides quick access to all main pages and login/register options.
- **Footer:** Displays website purpose, important links, and contact information.
- **Results page:** Lets users view election results, either personalized (filtered) or general (explore tab).
- **Forum page:** Users can view, start, and reply to discussion topics; includes moderation.

**Should have:**
- **Learn page:** Explains the Dutch political system in 5 simple steps with visual aids and interactive elements.
- **About page:** Describes the website’s purpose, features, and target audience.
- **Admin page:** Allows admins to manage users and content for platform safety.
- **Forum categories and filtering:** Users can filter forum posts by category for easier navigation.

**Could have:**
- **Party Match Helper:** Quiz-like tool to help users find the political party that best matches their views.
- **Account page:** Users can edit personal details, change password, or delete their account.
- **Dutch political parties page:** Overview of all parties, with info and key standpoints for each.
- **Forum likes:** Users can give likes to forum topics to show appreciation.


## User Interface Design

- Wireframes per page
- Consistent styling
- User-friendly navigation
- Responsive design

## Integrations & Interfaces

- Database for storing users, forum, quiz results
- API for communication between frontend and backend (REST, JSON)
- Import XML data for results (no CSV!)
- JWT for authentication and authorization (custom implementation, no Spring Security)


## User Stories per Page (categorized by MoSCoW)
**M** = **Must Have**

**S** = **Should Have**

**C** = **Could Have**

### **Requirements & User Stories:**

### **Home page (Must have)**

**1. Election Results Chart (Must have)**  
*Requirement:* Show a chart with the latest election results, displaying political parties and their number of seats.

**M** *User story:* As a user, I want to see a visual chart of the most recent election results, so I can quickly understand how many seats each party has won.


**2. Dashboard with tabs for Insights and Community (Must have)**  
*Requirement:* Show dashboard with tabs for Insights and Community.

**M** *User story:* As a user, I want to see a dashboard so I can easily find the main features of the website.

**3. Insights tab (Must have)**  
*Requirement:* Show Insights tab with regional voting results and party performance for a few selected regions; provide a 'More' button to view additional regions and detailed parliamentary data on a separate page.

**M** *User story 1:* As a user, I want to see voting results for a few regions on the dashboard, so I get a quick overview.

**M** *User story 2:* As a user, I want to click a 'More' button to view more regions and detailed data, so I can explore further if I want.

**4. Community tab (Must have)**  
*Requirement:* Show Community tab with recent forum posts and scroll function.

**M** *User story 1:* As a user, I want to see the most recent forum post on the dashboard, so I can quickly check what's new in the community.

**M** *User story 2:* As a user, I want to scroll through multiple recent forum posts, so I can read more discussions without leaving the dashboard.

**5. Learn tab (Should Have)**  
*Requirement:* Show Learn tab with a short, visual teaser and link to the Learn page, highlighting what users can learn there.

**S** *User story:* As a first-time voter, I want to see a teaser of what I can learn about the Dutch political system, so I know what to expect before visiting the Learn page.

**6. Navigation (Must have)**  
*Requirement:* Navigation to other main pages (Forum, Results, Learn, About)

**M** *User story:* As a user, I want to navigate easily to other pages, so I can find more information and features.

---

### **Login page (Must have)**
*Requirement 1:* Users can log in with their email and password.

**M** *User story 1:* As a user, I want to log in with my email and password, so I can access my account and use all features.

*Requirement 2:* Users receive feedback if login fails (e.g., wrong password).

**M** *User story 2:* As a user, I want to see a clear error message if my login fails, so I know what went wrong and can try again.

*Requirement 3:* Users who do not have an account can easily navigate from the login page to the register page  
**M** *User story 3:* As a user, I want to see a clear link or button to the register page if I do not have an account, so I can easily create one.

---

### **Register page (Must have)**
*Requirement 1:* Users can create a new account with email, password, and basic details.

**M** *User story 1:* As a new user, I want to register with my email and password, so I can create an account and start using the platform.

*Requirement 2:* Users receive confirmation after successful registration.

**M** *User story 2:* As a new user, I want to see a confirmation after registering, so I know my account was created successfully.

*Requirement 3:* Users receive clear error feedback if registration fails (e.g., weak password, email already in use).

**M** *User story 3:* As a new user, I want to see a clear error message if my password is too weak or my email is already in use, so I know what to fix and can try again.

---

### **Navigation bar (Must have)**
*Requirement 1:* Show a navigation bar with links to all main pages (Home, Forum, Results, Learn, About).

**M** *User story 1:* As a user, I want to use a navigation bar to quickly access all main pages, so I can easily find the information I need.

*Requirement 2:* Provide login/register button in the navigation bar, linking to a dedicated login/register page.

**M** *User story 2:* As a user, I want to access the login/register page from the navigation bar, so I can sign in or create an account easily.

---

### **Footer (Must have)**
*Requirement 1:* Show a brief description of the website’s purpose in the footer.

**M** *User story 1:* As a user, I want to quickly read what the website is for in the footer, so I understand its main goal.

*Requirement 2:* Provide a 'Explore' section with quick links to all main pages.

**M** *User story 2:* As a user, I want to find all important links in one place in the footer, so I can navigate easily.

*Requirement 3:* Show an information box with a link to the About page, Privacy Policy, Disclaimer, and Accessibility statement.

**M** *User story 3:* As a user, I want to access the About page, privacy policy, disclaimer, and accessibility info from the footer, so I know the website’s rules and background.

*Requirement 4:* Provide a contact section with contact details.

**M** *User story 4:* As a user, I want to find contact information in the footer, so I know how to get support or ask questions.

---

### **Results page (Must have)**

**1. Personalized tab**  
*Requirement:* Show a tab where users can use a quiz-like filter to select what data they want to see (e.g., party, region) and display the filtered results with relevant charts, visualizations, and numerical data.

**M** *User story 1:* As a user, I want to answer questions about what data I want to see, so I get personalized election results and insights.

**M** *User story 2:* As a user, I want to filter results by party, region so I can focus on what interests me most.

**M** *User story 3:* As a user, I want to see the filtered results as charts, visualizations, and numbers, so I can easily understand the data in different formats.

**2. Explore tab**  
*Requirement:* Show a tab with default data view, allowing users to freely browse election data without any filters.

**M** *User story 1:* As a user, I want to see the national election results by default, so I can view the overall distribution of seats in the parliament.

**M** *User story 2:* As a user, I want to see the full list of political parties and their number of seats, so I understand the composition of the parliament.

---

### **Forum page (Must have)**
*Requirement 1:* Show a list of recent forum topics and posts.

**M** *User story 1:* As a user, I want to see the latest forum topics and posts, so I can stay updated on community discussions.
    
*Requirement 2:* Allow users to start a new discussion topic.

**M** *User story 2:* As a user, I want to start a new topic in the forum, so I can ask questions or share my opinion.

*Requirement 3:* Allow users to reply to existing topics and posts.

**M** *User story 3:* As a user, I want to reply to forum posts, so I can join conversations and help others.
  
**S** *Requirement 4:* Posts can be assigned to categories (e.g., Politics, Elections, Parties, Voting Process) and users can filter posts by category.

**S** *User story 4:* As a user, I want to see and filter forum posts by category, so I can easily find discussions that match my interests.


**C** *Requirement 5:* Users can give likes to forum topics. 

**C** *User story 5:* As a user, I want to give a like to a forum topic, so I can show my appreciation for useful or interesting discussions.

---

### **Learn page (Should have)**
*Requirement 1:* The Learn page provides a simple, clear explanation of the Dutch political system in 5 steps, using visual aids and interactive elements to make the process easy to understand for young users.

**S** *User story 1:* As a user, I want to learn how the Dutch political system works in 5 simple steps, so I can quickly understand the basics without getting overwhelmed.

---

### **About page (Should have)**
*Requirement:* The About page provides a simple, clear explanation of what the website is, its main purpose, and what users can do with it. It highlights the features, target audience, and how the platform helps young people learn about Dutch politics and elections. 

**S** *User story:* As a user, I want to read a short, clear summary about the website and its features, so I know what I can do and how it can help me.

---

### **Admin page (Should have)**
*Requirement 1:* Admins can view and manage all user accounts (edit, deactivate, delete).
**S** *User story 1:* As an admin, I want to manage user accounts, so I can keep the platform safe and remove inappropriate users.

*Requirement 2:* Admins can moderate the forum (delete posts, block users).
**S** *User story 2:* As an admin, I want to moderate the forum, so I can keep discussions respectful and safe for all users.

---

### **Party Match Helper (Could have)**
*Requirement 1:* Users can use a Party Match Helper to find the political party that best matches their views. The tool asks questions about various topics, and users answer with options like 'Very important', 'Important', 'Less priority', etc.

**C** *User story 1:* As a user, I want to answer questions about political topics and see which party matches my views, so I can make a more informed voting choice.

---

### **Account page (Could have)**
*Requirement 1:* Users can edit their personal details.

**C** *User story 1:* As a user, I want to edit my personal details, so I can keep my information up-to-date.

*Requirement 2:* Users can change their password.  
**C** *User story 2:* As a user, I want to change my password, so I can keep my account secure.

*Requirement 3:* Users can delete their account.

**C** *User story 3:* As a user, I want to delete my account, so I can remove my data from the platform if I choose.

---

### **Dutch political parties page (Could have)**
*Requirement 1:* Users can view a list of all Dutch political parties.

**C** *User story 1:* As a user, I want to see all Dutch political parties, so I can explore my options.

*Requirement 2:* Users can click on a party to view detailed party information.

**C** *User story 2:* As a user, I want to click on a party to see more information, so I can learn about their background and values.

*Requirement 3:* Users can view the key standpoints of each party.

**C** *User story 3:* As a user, I want to see the most important standpoints of each party, so I can compare their positions easily.

---

## Acceptance criteria
- User can successfully register, log in, use the forum, view informative pages, results, and history
- JWT authentication works correctly
- Results from XML are processed and displayed correctly
- Application is responsive and user-friendly
- Privacy and security are ensured
- Website meets WCAG 2.1 AA accessibility standards