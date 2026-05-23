import React, { useState, useEffect, useRef } from 'react';
import { 
  Send, 
  Cpu, 
  Zap, 
  ShieldCheck, 
  BarChart3, 
  DollarSign, 
  Clock, 
  User,
  Bot,
  RefreshCw
} from 'lucide-react';
import { generateResponse, fetchMetrics } from './api';
import type { MetricsResponse } from './types';

interface Message {
  id: string;
  role: 'user' | 'assistant';
  content: string;
  timestamp: Date;
}

const App: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [prompt, setPrompt] = useState('');
  const [userId, setUserId] = useState('user-123');
  const [provider, setProvider] = useState('openai');
  const [isLoading, setIsLoading] = useState(false);
  const [metrics, setMetrics] = useState<MetricsResponse | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    const loadMetrics = async () => {
      try {
        const data = await fetchMetrics();
        setMetrics(data);
      } catch (error) {
        console.error('Failed to load metrics:', error);
      }
    };

    loadMetrics();
    const interval = setInterval(loadMetrics, 5000);
    return () => clearInterval(interval);
  }, []);

  const handleSend = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!prompt.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      role: 'user',
      content: prompt,
      timestamp: new Date(),
    };

    setMessages(prev => [...prev, userMessage]);
    setPrompt('');
    setIsLoading(true);

    try {
      const response = await generateResponse({
        userId,
        prompt: userMessage.content,
        provider
      });

      const assistantMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: response.answer,
        timestamp: new Date(),
      };

      setMessages(prev => [...prev, assistantMessage]);
      
      // Refresh metrics immediately after a successful call
      const updatedMetrics = await fetchMetrics();
      setMetrics(updatedMetrics);
    } catch (error) {
      console.error('Generation failed:', error);
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        role: 'assistant',
        content: 'Sorry, I encountered an error while processing your request.',
        timestamp: new Date(),
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex h-screen bg-slate-50 font-sans text-slate-900">
      {/* Main Chat Area */}
      <div className="flex flex-col flex-1 border-r border-slate-200">
        <header className="bg-white border-b border-slate-200 px-6 py-4 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-indigo-600 p-2 rounded-lg text-white">
              <Cpu size={24} />
            </div>
            <div>
              <h1 className="text-xl font-bold text-slate-800">LLM Optimizer</h1>
              <p className="text-xs text-slate-500 flex items-center gap-1">
                <span className="w-2 h-2 bg-emerald-500 rounded-full"></span>
                System Operational
              </p>
            </div>
          </div>
          
          <div className="flex gap-4">
            <div className="flex flex-col">
              <label className="text-[10px] uppercase font-bold text-slate-400 mb-1">User ID</label>
              <div className="flex items-center gap-2 bg-slate-100 px-3 py-1.5 rounded-md border border-slate-200">
                <User size={14} className="text-slate-400" />
                <input 
                  type="text" 
                  value={userId}
                  onChange={(e) => setUserId(e.target.value)}
                  className="bg-transparent border-none outline-none text-sm w-24"
                />
              </div>
            </div>
            <div className="flex flex-col">
              <label className="text-[10px] uppercase font-bold text-slate-400 mb-1">Provider</label>
              <select 
                value={provider}
                onChange={(e) => setProvider(e.target.value)}
                className="bg-slate-100 px-3 py-1.5 rounded-md border border-slate-200 text-sm outline-none cursor-pointer"
              >
                <option value="openai">OpenAI</option>
                <option value="gemini">Gemini</option>
                <option value="claude">Claude</option>
              </select>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-6 space-y-6">
          {messages.length === 0 ? (
            <div className="h-full flex flex-col items-center justify-center text-slate-400 space-y-4">
              <Bot size={48} className="opacity-20" />
              <p>Start a conversation to see the optimizer in action.</p>
            </div>
          ) : (
            messages.map((msg) => (
              <div 
                key={msg.id} 
                className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div className={`max-w-[80%] flex gap-3 ${msg.role === 'user' ? 'flex-row-reverse' : ''}`}>
                  <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
                    msg.role === 'user' ? 'bg-indigo-100 text-indigo-600' : 'bg-slate-100 text-slate-600'
                  }`}>
                    {msg.role === 'user' ? <User size={16} /> : <Bot size={16} />}
                  </div>
                  <div className={`p-4 rounded-2xl ${
                    msg.role === 'user' 
                      ? 'bg-indigo-600 text-white rounded-tr-none' 
                      : 'bg-white border border-slate-200 text-slate-800 rounded-tl-none shadow-sm'
                  }`}>
                    <p className="text-sm leading-relaxed">{msg.content}</p>
                    <span className={`text-[10px] mt-2 block opacity-50 ${msg.role === 'user' ? 'text-right' : 'text-left'}`}>
                      {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                </div>
              </div>
            ))
          )}
          {isLoading && (
            <div className="flex justify-start">
              <div className="bg-white border border-slate-200 p-4 rounded-2xl rounded-tl-none shadow-sm flex items-center gap-3">
                <RefreshCw size={16} className="animate-spin text-indigo-600" />
                <p className="text-sm text-slate-500 italic">Optimizing & fetching response...</p>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <form onSubmit={handleSend} className="p-6 bg-white border-t border-slate-200">
          <div className="relative">
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="Type your message here..."
              className="w-full bg-slate-50 border border-slate-200 rounded-xl px-4 py-3 pr-12 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all resize-none h-24"
              onKeyDown={(e) => {
                if (e.key === 'Enter' && !e.shiftKey) {
                  e.preventDefault();
                  handleSend(e);
                }
              }}
            />
            <button
              type="submit"
              disabled={isLoading || !prompt.trim()}
              className="absolute right-3 bottom-3 p-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors shadow-lg shadow-indigo-500/30"
            >
              <Send size={18} />
            </button>
          </div>
          <p className="text-[10px] text-slate-400 mt-2 text-center uppercase tracking-wider font-semibold">
            Powered by Multi-Level Caching & Request Batching
          </p>
        </form>
      </div>

      {/* Sidebar Dashboard */}
      <aside className="w-80 bg-white p-6 overflow-y-auto">
        <h2 className="text-lg font-bold text-slate-800 mb-6 flex items-center gap-2">
          <BarChart3 size={20} className="text-indigo-600" />
          Real-time Metrics
        </h2>

        <div className="space-y-4">
          <div className="bg-indigo-50 p-4 rounded-xl border border-indigo-100">
            <div className="flex justify-between items-start mb-2">
              <span className="text-indigo-600 bg-white p-2 rounded-lg">
                <Zap size={18} />
              </span>
              <span className="text-[10px] font-bold text-indigo-400 uppercase tracking-tighter">Total Requests</span>
            </div>
            <div className="text-2xl font-black text-indigo-900">{metrics?.totalRequests || 0}</div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="bg-emerald-50 p-4 rounded-xl border border-emerald-100">
              <div className="flex justify-between items-start mb-2">
                <span className="text-emerald-600 bg-white p-1.5 rounded-md">
                  <ShieldCheck size={16} />
                </span>
                <span className="text-[9px] font-bold text-emerald-400 uppercase">Exact Hits</span>
              </div>
              <div className="text-xl font-black text-emerald-900">{metrics?.exactCacheHits || 0}</div>
            </div>
            <div className="bg-blue-50 p-4 rounded-xl border border-blue-100">
              <div className="flex justify-between items-start mb-2">
                <span className="text-blue-600 bg-white p-1.5 rounded-md">
                  <Bot size={16} />
                </span>
                <span className="text-[9px] font-bold text-blue-400 uppercase">Semantic</span>
              </div>
              <div className="text-xl font-black text-blue-900">{metrics?.semanticCacheHits || 0}</div>
            </div>
          </div>

          <div className="bg-amber-50 p-4 rounded-xl border border-amber-100">
            <div className="flex justify-between items-center mb-1">
              <span className="text-[10px] font-bold text-amber-500 uppercase">Average Latency</span>
              <Clock size={14} className="text-amber-400" />
            </div>
            <div className="flex items-baseline gap-1">
              <span className="text-2xl font-black text-amber-900">
                {(metrics?.averageLatency ?? 0).toFixed(0)}
              </span>
              <span className="text-sm font-bold text-amber-600">ms</span>
            </div>
          </div>

          <div className="bg-slate-900 p-5 rounded-xl text-white shadow-xl">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-xs font-bold uppercase tracking-widest text-slate-400">Total Savings</h3>
              <DollarSign size={16} className="text-emerald-400" />
            </div>
            <div className="text-3xl font-black text-white mb-1">
              ${(metrics?.estimatedSavings ?? 0).toFixed(2)}
            </div>
            <p className="text-[10px] text-slate-400 font-medium">Estimated cost reduction based on cache hits.</p>
          </div>

          <div className="pt-4 mt-6 border-t border-slate-100">
            <div className="flex justify-between items-center mb-2">
              <span className="text-xs font-bold text-slate-500 uppercase">Efficiency Rate</span>
              <span className="text-xs font-bold text-indigo-600">{(metrics?.cacheHitRate ?? 0).toFixed(1)}%</span>
            </div>
            <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
              <div 
                className="bg-indigo-600 h-full transition-all duration-1000" 
                style={{ width: `${metrics?.cacheHitRate || 0}%` }}
              ></div>
            </div>
          </div>
        </div>

        <div className="mt-8 p-4 bg-slate-50 rounded-xl border border-slate-200">
          <h3 className="text-xs font-bold text-slate-800 uppercase mb-3">Optimization Stack</h3>
          <ul className="space-y-2">
            {[
              { label: 'Token Bucket', status: 'Active' },
              { label: 'Semantic Search', status: 'FAISS' },
              { label: 'Batcher', status: 'Sliding Window' },
              { label: 'Exact Cache', status: 'Caffeine' }
            ].map((item, i) => (
              <li key={i} className="flex justify-between items-center">
                <span className="text-[11px] text-slate-500">{item.label}</span>
                <span className="text-[9px] bg-slate-200 px-1.5 py-0.5 rounded font-bold text-slate-600">{item.status}</span>
              </li>
            ))}
          </ul>
        </div>
      </aside>
    </div>
  );
};

export default App;
