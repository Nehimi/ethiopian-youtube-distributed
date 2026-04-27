# Ethiopian YouTube - Distributed Video Streaming System

A distributed video streaming platform built with Java RMI, JavaFX, and MySQL that demonstrates fault tolerance and load balancing across multiple nodes - Ethiopian version of YouTube.

## 📋 Project Overview

This project implements a YouTube-like video streaming service with a distributed backend architecture. The system stores videos across multiple nodes and provides a modern JavaFX frontend for users to upload, browse, and stream videos.

## 🏗️ Architecture

### Backend (Week 1)
- **RMI Foundation**: Remote Method Invocation for distributed communication
- **File Streaming**: Byte-based video transfer over network
- **Database Integration**: MySQL for metadata management
- **Load Balancing**: Intelligent video distribution across nodes
- **Fault Tolerance**: System continues operating when nodes fail

### Frontend (Week 2)
- **JavaFX UI**: Modern, responsive interface
- **Media Player**: Integrated video playback
- **Glassmorphism Design**: Ultra-modern UI with CSS effects
- **File Upload**: Drag-and-drop video upload functionality

## 🛠️ Technology Stack

- **Language**: Java (JDK 17+)
- **Server OS**: Windows Server 2019
- **Networking**: Java RMI (Remote Method Invocation)
- **UI Framework**: JavaFX
- **Database**: MySQL (for metadata storage)
- **Storage**: Local file system (Node1, Node2, ... folders)
- **Styling**: CSS with Glassmorphism effects

## 📅 Development Timeline

### Week 1: Core Backend Implementation

#### Days 1-2: RMI Foundation
- [ ] Create `VideoInterface.java` - Define server operations (Upload, Download, List)
- [ ] Implement `VideoImpl.java` - File writing logic for Node1 and Node2 folders
- [ ] Set up basic RMI server-client communication

#### Days 3-4: File Streaming (Byte Management)
- [ ] Implement byte[] based video transfer over network
- [ ] Set up two separate servers on different ports
- [ ] Handle large file streaming efficiently
- [ ] Test video upload and download functionality

#### Days 5-7: Database Integration (Metadata)
- [ ] Set up MySQL database for video metadata
- [ ] Implement load balancer logic:
  - Google videos → Node1
  - Full videos → Node2
  - Additional distribution rules as needed
- [ ] Track which node contains each video
- [ ] Test database operations and node assignment

### Week 2: Frontend & Integration

#### Days 8-10: JavaFX UI Design
- [ ] Create main page with video thumbnails
- [ ] Implement upload modal with file selection
- [ ] Apply Glassmorphism CSS effects to buttons and cards
- [ ] Design responsive layout for different screen sizes

#### Days 11-12: Media Player & Integration
- [ ] Integrate JavaFX MediaView for video playback
- [ ] Connect frontend with RMI client
- [ ] Implement real-time video streaming from backend
- [ ] Add player controls (play, pause, seek, volume)

#### Days 13-14: Demo Preparation & Documentation
- [ ] Test fault tolerance by shutting down one server
- [ ] Verify system continues operating with remaining nodes
- [ ] Create architecture diagram
- [ ] Prepare comprehensive documentation
- [ ] Final testing and bug fixes

## 📁 Project Structure

```
DistributiveProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── server/
│   │   │   │   ├── VideoInterface.java
│   │   │   │   ├── VideoImpl.java
│   │   │   │   └── RMIServer.java
│   │   │   ├── client/
│   │   │   │   ├── RMIClient.java
│   │   │   │   └── VideoService.java
│   │   │   ├── database/
│   │   │   │   ├── DatabaseManager.java
│   │   │   │   └── VideoMetadata.java
│   │   │   └── ui/
│   │   │       ├── MainApplication.java
│   │   │       ├── VideoPlayerController.java
│   │   │       └── UploadController.java
│   │   └── resources/
│   │       ├── styles/
│   │       │   └── glassmorphism.css
│   │       └── fxml/
│   │           ├── main-view.fxml
│   │           └── upload-modal.fxml
├── Node1/           # Storage for first node
├── Node2/           # Storage for second node
├── database/
│   └── video_metadata.sql
├── README.md
└── pom.xml          # Maven configuration
```

## �️ Windows Server 2019 Setup

### Server Configuration
- **OS**: Windows Server 2019 Standard/Datacenter
- **Java**: Install JDK 17+ on server
- **MySQL**: Install MySQL Server 8.0+
- **Firewall**: Configure ports 1099, 1100 for RMI
- **File Permissions**: Set up Node1 and Node2 folders with proper access rights

### Server Deployment Steps
1. **Install Java Development Kit**
   ```cmd
   # Download and install JDK 17 from Oracle
   # Set JAVA_HOME environment variable
   ```

2. **Install MySQL Server**
   ```cmd
   # Install MySQL Server 8.0+
   # Create database and user for the application
   ```

3. **Configure Windows Firewall**
   ```cmd
   # Allow RMI ports 1099 and 1100
   netsh advfirewall firewall add rule name="RMI-Node1" dir=in action=allow protocol=TCP localport=1099
   netsh advfirewall firewall add rule name="RMI-Node2" dir=in action=allow protocol=TCP localport=1100
   ```

4. **Create Storage Directories**
   ```cmd
   mkdir C:\EthiopianYouTube\Node1
   mkdir C:\EthiopianYouTube\Node2
   # Set appropriate permissions
   ```

5. **Start RMI Registry**
   ```cmd
   # Start RMI registry on Windows Server
   start rmiregistry 1099
   start rmiregistry 1100
   ```

## � Getting Started

### Prerequisites
- JDK 17 or higher
- Windows Server 2019 (for backend servers)
- MySQL Server
- Maven
- JavaFX SDK

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd DistributiveProject
   ```

2. **Set up MySQL Database**
   ```sql
   CREATE DATABASE video_streaming;
   USE video_streaming;
   -- Run database/video_metadata.sql
   ```

3. **Configure Database Connection**
   - Update database credentials in `DatabaseManager.java`

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Start the Servers on Windows Server 2019**
   ```cmd
   # Start Node1 (Port 1099) on Windows Server
   java -cp target/classes server.RMIServer Node1 1099
   
   # Start Node2 (Port 1100) on Windows Server
   java -cp target/classes server.RMIServer Node2 1100
   ```

6. **Run the Application**
   ```bash
   java -cp target/classes ui.MainApplication
   ```

## 🔧 Configuration

### Server Paths (Windows Server 2019)
- Node1 Storage: `C:\EthiopianYouTube\Node1\`
- Node2 Storage: `C:\EthiopianYouTube\Node2\`
- Database: MySQL on Windows Server
- Logs: `C:\EthiopianYouTube\logs\`

### Server Ports
- Node1: 1099 (default RMI port)
- Node2: 1100
- Additional nodes can be configured as needed

### Database Settings
Update these in `DatabaseManager.java`:
- Host: localhost
- Port: 3306
- Database: video_streaming
- Username: root
- Password: [your-password]

## 🎯 Key Features

### Backend Features
- **Distributed Storage**: Videos stored across multiple nodes
- **Load Balancing**: Intelligent distribution based on video type
- **Fault Tolerance**: System continues operating when nodes fail
- **Metadata Management**: MySQL database tracks video locations
- **Byte Streaming**: Efficient large file transfer

### Frontend Features
- **Modern UI**: Glassmorphism design with smooth animations
- **Video Gallery**: Thumbnail-based video browsing
- **Media Player**: Full-featured video playback
- **Upload Interface**: Drag-and-drop file upload
- **Responsive Design**: Adapts to different screen sizes

## 🧪 Testing

### Fault Tolerance Test
1. Start both Node1 and Node2 servers
2. Upload videos to the system
3. Shut down Node1 server
4. Verify videos from Node2 are still accessible
5. Restart Node1 and verify synchronization

### Load Balancing Test
1. Upload different types of videos
2. Verify distribution across nodes follows the rules:
   - Google videos → Node1
   - Full videos → Node2
3. Check database for correct node assignments

## 📊 System Architecture Diagram

```
┌─────────────────┐    ┌─────────────────┐
│   JavaFX UI     │    │   Load Balancer │
│   (Client)      │◄──►│   (Logic)       │
└─────────┬───────┘    └─────────┬───────┘
          │                      │
          ▼                      ▼
┌─────────────────┐    ┌─────────────────┐
│   RMI Client    │    │   MySQL DB      │
└─────────┬───────┘    └─────────────────┘
          │
          ▼
┌─────────────────┐    ┌─────────────────┐
│     Node1       │    │     Node2       │
│   (Port 1099)   │    │   (Port 1100)   │
│   ┌─────────┐   │    │   ┌─────────┐   │
│   │Videos/  │   │    │   │Videos/  │   │
│   └─────────┘   │    │   └─────────┘   │
└─────────────────┘    └─────────────────┘
```

## 🔍 Monitoring & Debugging

### RMI Registry Status
```bash
# Check if RMI registry is running
rmiregistry &
```

### Database Queries
```sql
-- Check video distribution
SELECT node_name, COUNT(*) as video_count 
FROM video_metadata 
GROUP BY node_name;

-- Check system status
SELECT * FROM video_metadata ORDER BY upload_date DESC;
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Contact

For questions or support, please contact [your-email@example.com]

---

**Note**: This project is part of a distributed systems course and demonstrates advanced Java networking concepts with modern UI design.
