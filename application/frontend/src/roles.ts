export const ROLE_LABELS: Record<string, string> = {
  ADMIN: 'Admin', CUSTOMER: 'Customer', QUOTATION_STAFF: 'Quotation Staff',
  PROJECT_MANAGER: 'Project Manager', DESIGNER: 'Designer',
  MARKETING_MANAGER: 'Marketing Manager', SERVICE_STAFF: 'Service & Package Staff',
  FINANCE_MANAGER: 'Finance Manager', FINANCE_STAFF: 'Finance Staff',
  COMMUNICATION_STAFF: 'Communication Staff', CUSTOMER_RELATIONS_OFFICER: 'Customer Relations Officer',
};
export const roleLabel = (role: string | undefined) => ROLE_LABELS[role || ''] || role || '';
