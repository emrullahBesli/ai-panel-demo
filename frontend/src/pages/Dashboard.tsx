import React, { useState } from 'react';
import { Container, TextField, Button, Typography, Paper, Link } from '@mui/material';
import { chat } from '../services/api';

const Dashboard = () => {
    const [message, setMessage] = useState('');
    const [chatResponse, setChatResponse] = useState<string | null>(null);
    const [downloadUrl, setDownloadUrl] = useState<string | null>(null);

    const handleChat = async () => {
        try {
            const res = await chat(message);
            // Assuming response structure: { chatResponse: string, downloadableContentUrl: string | null }
            setChatResponse(res.data.chatResponse);
            setDownloadUrl(res.data.downloadableContentUrl);
        } catch (error) {
            setChatResponse('Error: ' + error);
            setDownloadUrl(null);
        }
    };

    return (
        <Container>
            <Typography variant='h4'>Dashboard</Typography>
            <Paper style={{ padding: '20px', marginTop: '20px' }}>
                <TextField fullWidth value={message} onChange={(e) => setMessage(e.target.value)} label='Ask anything...' />
                <Button onClick={handleChat} variant='contained' style={{ marginTop: '10px' }}>Send</Button>
                
                {chatResponse && (
                    <Typography style={{ marginTop: '20px' }}>{chatResponse}</Typography>
                )}
                
                {downloadUrl && (
                    <Button 
                        variant='outlined' 
                        style={{ marginTop: '10px' }} 
                        component='a' 
                        href={downloadUrl} 
                        target='_blank' 
                        rel='noopener noreferrer'
                    >
                        Download Content
                    </Button>
                )}
            </Paper>
        </Container>
    );
};

export default Dashboard;
