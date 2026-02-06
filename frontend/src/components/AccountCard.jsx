import { FiCreditCard, FiSave, FiCheckCircle, FiXCircle } from 'react-icons/fi';

function AccountCard({ account, isSelected, onClick, delay = 0 }) {
  const isChecking = account.accountType === 'CHECKING';
  
  return (
    <div 
      onClick={onClick}
      className={`
        card p-5 cursor-pointer card-hover animate-slide-up
        ${isSelected ? 'ring-2 ring-bank-accent border-bank-accent' : 'hover:border-gray-300'}
      `}
      style={{ animationDelay: `${delay}s` }}
    >
      <div className="flex items-start justify-between mb-4">
        <div className="flex items-center gap-3">
          <div className={`
            w-12 h-12 rounded-xl flex items-center justify-center
            ${isChecking ? 'bg-blue-50' : 'bg-amber-50'}
          `}>
            {isChecking ? (
              <FiCreditCard className="text-2xl text-bank-accent" />
            ) : (
              <FiSave className="text-2xl text-amber-500" />
            )}
          </div>
          <div>
            <h3 className="font-medium text-gray-900">{account.accountHolderName}</h3>
            <p className="text-sm text-gray-500">{account.accountNumber}</p>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <span className={`
            text-xs px-2 py-1 rounded-full font-medium
            ${isChecking ? 'bg-blue-50 text-bank-accent' : 'bg-amber-50 text-amber-600'}
          `}>
            {isChecking ? 'Vadesiz' : 'Birikim'}
          </span>
          {account.active ? (
            <FiCheckCircle className="text-bank-success" />
          ) : (
            <FiXCircle className="text-bank-danger" />
          )}
        </div>
      </div>

      <div className="flex items-end justify-between">
        <div>
          <p className="text-sm text-gray-500 mb-1">Mevcut Bakiye</p>
          <p className="font-display text-2xl font-bold text-gray-900">
            ₺{parseFloat(account.balance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
          </p>
        </div>
        <div className="text-right">
          {isChecking ? (
            <>
              <p className="text-xs text-gray-400">Kullanılabilir</p>
              <p className="text-sm text-bank-success">
                ₺{parseFloat(account.availableBalance).toLocaleString('tr-TR', { minimumFractionDigits: 2 })}
              </p>
              <p className="text-xs text-gray-400 mt-1">
                Limit: ₺{parseFloat(account.overdraftLimit).toLocaleString('tr-TR')}
              </p>
            </>
          ) : (
            <>
              <p className="text-xs text-gray-400">Faiz Oranı</p>
              <p className="text-sm text-amber-600">%{account.interestRate}</p>
              <p className="text-xs text-gray-400 mt-1">
                Min: ₺{parseFloat(account.minimumBalance).toLocaleString('tr-TR')}
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
}

export default AccountCard;
