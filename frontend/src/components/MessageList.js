import React from 'react';

const MessageList = ({ messages, onDelete }) => {
    return (
        <div>
            <h2>Messages</h2>
            <ul>
                {messages.map((message) => (
                    <li key={message.id}>
                        <strong>{message.sender}</strong> to <strong>{message.recipient}</strong>: {message.content}
                        <button onClick={() => onDelete(message.id)}>Delete</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default MessageList;