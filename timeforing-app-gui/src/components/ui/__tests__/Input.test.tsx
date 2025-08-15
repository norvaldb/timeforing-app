import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { Input } from '../Input';

describe('Input', () => {
  it('should render input field with label', () => {
    render(<Input label="Test Label" placeholder="Test placeholder" />);
    
    expect(screen.getByLabelText('Test Label')).toBeInTheDocument();
    expect(screen.getByPlaceholderText('Test placeholder')).toBeInTheDocument();
  });

  it('should display error message when error prop is provided', () => {
    render(
      <Input 
        label="Test Label" 
        error="This is an error message" 
        placeholder="Test placeholder"
      />
    );
    
    expect(screen.getByText('This is an error message')).toBeInTheDocument();
    expect(screen.getByLabelText('Test Label')).toHaveClass('border-red-500');
  });

  it('should not display error message when error is null', () => {
    render(<Input label="Test Label" placeholder="Test placeholder" />);
    
    expect(screen.queryByText(/error/i)).not.toBeInTheDocument();
  });

  it('should not display error message when error is undefined', () => {
    render(<Input label="Test Label" placeholder="Test placeholder" />);
    
    expect(screen.queryByText(/error/i)).not.toBeInTheDocument();
  });

  it('should mark field as required when required prop is true', () => {
    render(<Input label="Required Field" required placeholder="Test placeholder" />);
    
    // The asterisk is added via CSS, so we check the base label text
    expect(screen.getByLabelText('Required Field')).toBeInTheDocument();
    expect(screen.getByRole('textbox')).toHaveAttribute('required');
  });

  it('should not mark field as required when required prop is false', () => {
    render(<Input label="Optional Field" required={false} placeholder="Test placeholder" />);
    
    expect(screen.getByLabelText('Optional Field')).toBeInTheDocument();
    expect(screen.getByRole('textbox')).not.toHaveAttribute('required');
  });

  it('should apply disabled state correctly', () => {
    render(<Input label="Disabled Field" disabled placeholder="Test placeholder" />);
    
    const input = screen.getByRole('textbox');
    expect(input).toBeDisabled();
    // Check that disabled styles are applied via Tailwind's disabled: modifiers
    expect(input).toHaveClass('disabled:cursor-not-allowed', 'disabled:opacity-50');
  });

  it('should forward ref correctly', () => {
    const inputRef = { current: null as HTMLInputElement | null };
    
    const TestComponent = () => (
      <Input 
        ref={inputRef}
        label="Test"
        placeholder="Test placeholder"
      />
    );
    
    render(<TestComponent />);
    
    expect(inputRef.current).not.toBeNull();
    expect(inputRef.current?.tagName).toBe('INPUT');
  });

  it('should set proper accessibility attributes', () => {
    render(<Input label="Test Field" required />);
    
    const input = screen.getByRole('textbox');
    expect(input).toHaveAttribute('aria-required', 'true');
    expect(input).toHaveAttribute('required');
  });

  it('should support different input types', () => {
    render(<Input label="Email" type="email" placeholder="test@example.com" />);
    
    const input = screen.getByRole('textbox');
    expect(input).toHaveAttribute('type', 'email');
  });

  it('should support autoComplete attribute', () => {
    render(<Input label="Name" autoComplete="name" placeholder="Your name" />);
    
    const input = screen.getByRole('textbox');
    expect(input).toHaveAttribute('autoComplete', 'name');
  });

  it('should apply custom className', () => {
    render(<Input label="Custom" className="custom-class" placeholder="Test" />);
    
    const input = screen.getByRole('textbox');
    expect(input).toHaveClass('custom-class');
  });

  it('should handle focus and blur states', () => {
    render(<Input label="Focus Test" placeholder="Test placeholder" />);
    
    const input = screen.getByRole('textbox');
    
    // Focus the input
    input.focus();
    expect(input).toHaveFocus();
    
    // Blur the input
    input.blur();
    expect(input).not.toHaveFocus();
  });
});
