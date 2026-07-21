import React, { useState, useEffect } from 'react';
import { Container, Table, TableBody, TableCell, TableHead, TableRow, Button, Dialog, DialogTitle, DialogContent, TextField, DialogActions, Paper, Typography } from '@mui/material';
import { listUsers, createUser, listOrders, createOrder, listOrderUsages, createOrderUsage } from '../services/api';

const Entities = () => {
    const [users, setUsers] = useState<any[]>([]);
    const [orders, setOrders] = useState<any[]>([]);
    const [usages, setUsages] = useState<any[]>([]);
    const [open, setOpen] = useState(false);
    const [entityType, setEntityType] = useState<'' | 'user' | 'order' | 'usage'>('');
    const [formData, setFormData] = useState<any>({});

    const fetchData = async () => {
        const u = await listUsers(); setUsers(u.data);
        const o = await listOrders(); setOrders(o.data);
        const us = await listOrderUsages(); setUsages(us.data);
    };

    useEffect(() => { fetchData(); }, []);

    const handleAdd = async () => {
        if (entityType === 'user') await createUser(formData.username, formData.email);
        else if (entityType === 'order') await createOrder(formData.description, formData.paidAmount, formData.userId);
        else if (entityType === 'usage') await createOrderUsage(formData.orderId, formData.usageDetails);
        setOpen(false);
        setFormData({});
        fetchData();
    };

    return (
        <Container>
            <Typography variant='h4' gutterBottom>Entities Management</Typography>
            
            <Typography variant='h6'>Users</Typography>
            <Table component={Paper}><TableHead><TableRow><TableCell>ID</TableCell><TableCell>Username</TableCell><TableCell>Email</TableCell></TableRow></TableHead><TableBody>{users.map(u => <TableRow key={u.id}><TableCell>{u.id}</TableCell><TableCell>{u.username}</TableCell><TableCell>{u.email}</TableCell></TableRow>)}</TableBody></Table>
            <Button onClick={() => { setEntityType('user'); setOpen(true); }}>Add User</Button>

            <Typography variant='h6' sx={{mt: 4}}>Orders</Typography>
            <Table component={Paper}><TableHead><TableRow><TableCell>ID</TableCell><TableCell>Desc</TableCell><TableCell>Amount</TableCell><TableCell>User ID</TableCell></TableRow></TableHead><TableBody>{orders.map(o => <TableRow key={o.id}><TableCell>{o.id}</TableCell><TableCell>{o.description}</TableCell><TableCell>{o.paidAmount}</TableCell><TableCell>{o.user.id}</TableCell></TableRow>)}</TableBody></Table>
            <Button onClick={() => { setEntityType('order'); setOpen(true); }}>Add Order</Button>

            <Typography variant='h6' sx={{mt: 4}}>Order Usages</Typography>
            <Table component={Paper}><TableHead><TableRow><TableCell>ID</TableCell><TableCell>Order ID</TableCell><TableCell>Details</TableCell><TableCell>Cost</TableCell></TableRow></TableHead><TableBody>{usages.map(us => <TableRow key={us.id}><TableCell>{us.id}</TableCell><TableCell>{us.order.id}</TableCell><TableCell>{us.usageDetails}</TableCell><TableCell>{us.costPrice}</TableCell></TableRow>)}</TableBody></Table>
            <Button onClick={() => { setEntityType('usage'); setOpen(true); }}>Add Usage</Button>

            <Dialog open={open} onClose={() => setOpen(false)}>
                <DialogTitle>Add {entityType}</DialogTitle>
                <DialogContent>
                    {entityType === 'user' && (
                        <><TextField fullWidth label='Username' onChange={e => setFormData({...formData, username: e.target.value})} /><TextField fullWidth label='Email' onChange={e => setFormData({...formData, email: e.target.value})} /></>
                    )}
                    {entityType === 'order' && (
                        <><TextField fullWidth label='Description' onChange={e => setFormData({...formData, description: e.target.value})} /><TextField fullWidth label='Amount' type='number' onChange={e => setFormData({...formData, paidAmount: parseFloat(e.target.value)})} /><TextField fullWidth label='User ID' type='number' onChange={e => setFormData({...formData, userId: parseInt(e.target.value)})} /></>
                    )}
                    {entityType === 'usage' && (
                        <><TextField fullWidth label='Order ID' type='number' onChange={e => setFormData({...formData, orderId: parseInt(e.target.value)})} /><TextField fullWidth label='Details' onChange={e => setFormData({...formData, usageDetails: e.target.value})} /></>
                    )}
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>Cancel</Button>
                    <Button onClick={handleAdd}>Add</Button>
                </DialogActions>
            </Dialog>
        </Container>
    );
};

export default Entities;
