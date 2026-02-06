import { useState } from 'react';
import { FiX, FiTrendingUp, FiTrendingDown } from 'react-icons/fi';

function TransactionModal({ type, onClose, onSubmit }) {
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');

  const isDeposit = type === 'deposit';

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(parseFloat(amount), description);
  };

  return (
    <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50 p-4">
      <div className="card w-full max-w-md animate-slide-up">
        <div className="flex items-center justify-between p-5 border-b border-gray-100">
          <div className="flex items-center gap-3">
            <div className={`
              w-10 h-10 rounded-xl flex items-center justify-center
              ${isDeposit ? 'bg-green-50' : 'bg-red-50'}
            `}>
              {isDeposit ? (
                <FiTrendingUp className="text-xl text-bank-success" />
              ) : (
                <FiTrendingDown className="text-xl text-bank-danger" />
              )}
            </div>
            <h2 className="font-display text-xl font-semibold text-gray-900">
              {isDeposit ? 'Para Yatır' : 'Para Çek'}
            </h2>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-gray-500">
            <FiX />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="p-5 space-y-4">
          <div>
            <label className="block text-sm text-gray-500 mb-2">Tutar (₺)</label>
            <input
              type="number"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
              min="0.01"
              step="0.01"
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 text-2xl font-bold focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="0.00"
              autoFocus
            />
          </div>

          <div>
            <label className="block text-sm text-gray-500 mb-2">Açıklama (Opsiyonel)</label>
            <input
              type="text"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              className="w-full bg-gray-50 border border-gray-200 rounded-lg px-4 py-3 focus:outline-none focus:border-bank-accent focus:ring-1 focus:ring-bank-accent transition-colors text-gray-900"
              placeholder="İşlem açıklaması..."
            />
          </div>

          <div className="grid grid-cols-4 gap-2">
            {[100, 250, 500, 1000].map((val) => (
              <button
                key={val}
                type="button"
                onClick={() => setAmount(val.toString())}
                className="py-2 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors text-sm text-gray-700"
              >
                ₺{val}
              </button>
            ))}
          </div>

          <button
            type="submit"
            className={`
              w-full py-3 rounded-lg font-medium transition-colors text-white
              ${isDeposit 
                ? 'bg-bank-success hover:bg-green-600' 
                : 'bg-bank-danger hover:bg-red-600'}
            `}
          >
            {isDeposit ? 'Para Yatır' : 'Para Çek'}
          </button>
        </form>
      </div>
    </div>
  );
}

export default TransactionModal;
