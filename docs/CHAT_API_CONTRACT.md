# Chat API Contract - Backend Documentation

## 1. Data Models

### Conversation (ChatRoom)
```json
{
    "room_id": 1,
    "conversation_id": "conv_uuid_123",
    "user_ids": [1, 2],
    "other_user_id": 2,
    "other_user_name": "Nguyễn Văn A",
    "other_user_avatar": "https://...",
    "last_message": "Xin chào!",
    "last_message_time": "2024-01-15 10:30:00",
    "last_message_sender_id": 1,
    "unread_count": 3,
    "is_online": true,
    "last_seen": "2024-01-15 10:25:00",
    "created_at": "2024-01-10 08:00:00"
}
```

### Message (ChatMessage)
```json
{
    "id": 1,
    "message_id": "msg_uuid_456",
    "room_id": 1,
    "sender_id": 1,
    "sender_name": "Nguyễn Văn B",
    "message": "Xin chào!",
    "type": "text",
    "status": 3,
    "is_read": true,
    "is_mine": true,
    "created_at": "2024-01-15 10:30:00",
    "delivered_at": "2024-01-15 10:30:05",
    "seen_at": "2024-01-15 10:31:00"
}
```

### Message Status Codes
- `0` - SENDING (đang gửi)
- `1` - SENT (đã gửi đến server)
- `2` - DELIVERED (đã gửi đến thiết bị người nhận)
- `3` - SEEN (đã xem)
- `-1` - FAILED (gửi thất bại)

### Message Types
- `text` - Tin nhắn văn bản
- `image` - Hình ảnh
- `file` - File đính kèm

---

## 2. API Endpoints

### 2.1 Get Chat Rooms
```
GET /api/chat/get_rooms.php
Authorization: Bearer {token}
```

**Response:**
```json
{
    "success": true,
    "data": [
        {
            "room_id": 1,
            "other_user_id": 2,
            "other_user_name": "Nguyễn Văn A",
            "last_message": "Xin chào!",
            "last_message_time": "2024-01-15 10:30:00",
            "unread_count": 3,
            "is_online": true
        }
    ]
}
```

### 2.2 Get Messages (with Pagination)
```
GET /api/chat/get_messages.php?room_id={id}&page={page}&limit={limit}
Authorization: Bearer {token}
```

**Response:**
```json
{
    "success": true,
    "data": [...messages],
    "has_more": true,
    "total": 150,
    "page": 1,
    "limit": 30
}
```

### 2.3 Send Message
```
POST /api/chat/send_message.php
Authorization: Bearer {token}
Content-Type: application/json

{
    "room_id": 1,
    "message": "Xin chào!",
    "message_id": "uuid-client-generated",
    "type": "text"
}
```

**Response:**
```json
{
    "success": true,
    "message": "Tin nhắn đã được gửi",
    "data": {
        "id": 123,
        "message_id": "uuid-client-generated",
        "status": 1
    }
}
```

### 2.4 Mark Messages as Read
```
POST /api/chat/mark_read.php
Authorization: Bearer {token}
Content-Type: application/json

{
    "room_id": 1,
    "last_read_message_id": 123
}
```

### 2.5 Update Message Status (Delivered/Seen)
```
POST /api/chat/update_status.php
Authorization: Bearer {token}
Content-Type: application/json

{
    "message_ids": [1, 2, 3],
    "status": 2
}
```

### 2.6 Delete Message
```
POST /api/chat/delete_message.php
Authorization: Bearer {token}
Content-Type: application/json

{
    "message_id": 123,
    "delete_for": "me"
}
```

---

## 3. Realtime Events (WebSocket/Firebase)

### 3.1 New Message Event
```json
{
    "event": "new_message",
    "data": {
        "room_id": 1,
        "message": {...}
    }
}
```

### 3.2 Typing Event
```json
{
    "event": "typing",
    "data": {
        "room_id": 1,
        "user_id": 2,
        "is_typing": true
    }
}
```

### 3.3 Message Status Update Event
```json
{
    "event": "message_status",
    "data": {
        "message_id": "uuid",
        "status": 3
    }
}
```

### 3.4 Online Status Event
```json
{
    "event": "online_status",
    "data": {
        "user_id": 2,
        "is_online": true,
        "last_seen": "2024-01-15 10:30:00"
    }
}
```

---

## 4. Message Flow

### Gửi tin nhắn:
1. Client tạo message với UUID và status = SENDING
2. Hiển thị optimistic UI
3. Gửi request đến server
4. Server lưu message, trả về success
5. Client cập nhật status = SENT
6. Server push notification đến người nhận
7. Người nhận online → status = DELIVERED
8. Người nhận mở chat → status = SEEN

### Nhận tin nhắn:
1. Realtime listener nhận event new_message
2. Thêm message vào danh sách
3. Cập nhật unread count
4. Nếu đang mở chat → gửi seen event
5. Nếu không → hiển thị notification

---

## 5. Database Schema (MySQL)

### chat_rooms
```sql
CREATE TABLE chat_rooms (
    id INT PRIMARY KEY AUTO_INCREMENT,
    conversation_id VARCHAR(50) UNIQUE,
    user1_id INT NOT NULL,
    user2_id INT NOT NULL,
    last_message_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user1_id) REFERENCES users(id),
    FOREIGN KEY (user2_id) REFERENCES users(id)
);
```

### chat_messages
```sql
CREATE TABLE chat_messages (
    id INT PRIMARY KEY AUTO_INCREMENT,
    message_id VARCHAR(50) UNIQUE,
    room_id INT NOT NULL,
    sender_id INT NOT NULL,
    message TEXT NOT NULL,
    type ENUM('text', 'image', 'file') DEFAULT 'text',
    status TINYINT DEFAULT 1,
    is_deleted_sender BOOLEAN DEFAULT FALSE,
    is_deleted_receiver BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP NULL,
    seen_at TIMESTAMP NULL,
    FOREIGN KEY (room_id) REFERENCES chat_rooms(id),
    FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

### user_online_status
```sql
CREATE TABLE user_online_status (
    user_id INT PRIMARY KEY,
    is_online BOOLEAN DEFAULT FALSE,
    last_seen TIMESTAMP,
    fcm_token VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 6. Notes for Implementation

### Realtime Options:
1. **Firebase Realtime Database** - Dễ implement, free tier tốt
2. **Firebase Cloud Messaging** - Cho push notifications
3. **WebSocket (Ratchet PHP)** - Self-hosted, full control
4. **Pusher** - Third-party, dễ dùng

### Security:
- Validate room membership trước khi cho phép đọc/gửi
- Rate limiting cho send message
- Sanitize message content
- Validate file uploads

### Performance:
- Index trên room_id, sender_id, created_at
- Pagination cho messages
- Cache online status
- Batch update cho seen status
