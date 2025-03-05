import React, { useState } from 'react';

const MessageForm = ({ onSend }) => {
    const [sender, setSender] = useState('');
    const [recipient, setRecipient] = useState('');
    const [content, setContent] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSend({ sender, recipient, content });
        setSender('');
        setRecipient('');
        setContent('');
    };

    return (
        <form onSubmit={handleSubmit}>
            <h2>Send a Message</h2>
            <div>
                <label>Sender:</label>
                <input
                    type="text"
                    value={sender}
                    onChange={(e) => setSender(e.target.value)}
                    required
                />
            </div>
            <div>
                <label>Recipient:</label>
                <input
                    type="text"
                    value={recipient}
                    onChange={(e) => setRecipient(e.target.value)}
                    required
                />
            </div>
            <div>
                <label>Content:</label>
                <textarea
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    required
                />
            </div>
            <button type="submit">Send</button>
        </form>
    );
};

export default MessageForm;