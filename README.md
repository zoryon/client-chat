## Command Structure

| Command Code     | Description                                 |
|------------------|---------------------------------------------|
| `USR_INIT`       | Initialize user session (NEW or OLD)        |
| `REQ_CHATS`      | Request information about user chats (userId) |
| `DEL_USER`       | Request to delete user information          |
| `MENU_BACK`      | Return to the main menu                     |
| `NAV_CHAT`       | Navigate to specified chat or group         |
| `SEND_MSG`       | Send a new message                          |
| `RM_MSG`         | Remove a message                            |
| `UPD_NAME`       | Change username                             |
| `VIEW_ALL_USERS` | View all users in the system                |
| `VIEW_ALL_GROUPS`| View all groups in the system               |

## Error Messages

| Error Code       | Description                                 |
|------------------|---------------------------------------------|
| `ERR_GEN`        | General server error                        |
| `ERR_NOT_FOUND`  | User not found                              |
| `ERR_CHAT_EXISTS`| Private chat already exists                 |
| `ERR_USER_EXISTS`| Username already in use                     |
| `ERR_DISCONNECT` | Client has disconnected                     |

## Chat Management

| Command Code     | Description                                 |
|------------------|---------------------------------------------|
| `NEW_CHAT`       | Initiate a new private chat                 |
| `LEAVE_GROUP`    | Leave a group chat                          |
| `DEL_GROUP`      | Delete a group object                       |
| `ADD_ADMIN`      | Add a new admin to a group                  |
| `RM_ADMIN`       | Remove admin privileges from a user         |
| `UPD_GROUP_NAME` | Change the name of a group                  |
| `CREATE_GROUP`   | Create a new group                          |
| `VIEW_GROUP_USERS`| View users in a specific group             |
